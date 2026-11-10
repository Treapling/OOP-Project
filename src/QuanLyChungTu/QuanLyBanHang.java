package QuanLyChungTu;

import QuanLySanPham.DanhSachThuoc;
import QuanLySanPham.SanPhamYTe;
import QuanLySanPham.DonViTinh;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyBanHang {
    private final Map<String, PhieuXuat> invoices = new LinkedHashMap<>();
    private final Map<String, List<ChiTietPhieuXuat>> invoiceDetails = new LinkedHashMap<>();
    private final DanhSachThuoc medicineManager;
    private String filePath;

    public QuanLyBanHang(DanhSachThuoc medicineManager) {
        this.medicineManager = medicineManager;
    }

    public PhieuXuat createInvoice(String invoiceId, String customerId, String employeeId, String paymentMethod) {
        if (invoiceId == null || invoiceId.isBlank() || invoices.containsKey(invoiceId)) {
            return null;
        }
        PhieuXuat invoice = new PhieuXuat(invoiceId, customerId, employeeId, 0, paymentMethod, false);
        invoices.put(invoiceId, invoice);
        invoiceDetails.put(invoiceId, new ArrayList<>());
        saveIfConfigured();
        SystemLogger.ghiLog("Tạo hóa đơn " + invoiceId);
        return invoice;
    }

    public boolean addMedicineToInvoice(String invoiceId, String medicineId, int quantity) {
        SanPhamYTe medicine = medicineManager.getMedicine(medicineId);
        return medicine != null && addMedicineToInvoice(invoiceId, medicineId, quantity, medicine.getBaseUnitName());
    }

    public boolean addMedicineToInvoice(String invoiceId, String medicineId, int saleQuantity, String unitName) {
        PhieuXuat invoice = invoices.get(invoiceId);
        List<ChiTietPhieuXuat> detailList = invoiceDetails.get(invoiceId);
        if (invoice == null || detailList == null || invoice.isPaid()) {
            return false;
        }
        SanPhamYTe medicine = medicineManager.getMedicine(medicineId);
        DonViTinh unit = medicine == null ? null : medicine.getSellUnits().stream()
                .filter(candidate -> candidate.getName().equals(unitName)).findFirst().orElse(null);
        if (medicine == null || unit == null || saleQuantity <= 0) {
            return false;
        }
        final int baseQuantity;
        try {
            baseQuantity = Math.multiplyExact(saleQuantity, unit.getFactorToBase());
        } catch (ArithmeticException e) {
            return false;
        }
        Map<String, Integer> allocations = medicineManager.consumeStock(medicineId, baseQuantity);
        if (allocations == null) return false;

        ChiTietPhieuXuat detail = new ChiTietPhieuXuat(
                invoiceId + "-" + (detailList.size() + 1),
                invoiceId,
                medicineId,
                baseQuantity,
                medicine.tinhGiaBan() * unit.getFactorToBase()
        );
        detail.setSaleDetails(saleQuantity, unitName, allocations.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(java.util.stream.Collectors.joining(",")));
        detailList.add(detail);
        invoice.setTotalAmount(invoice.getTotalAmount() + detail.getLineTotal());
        saveIfConfigured();
        SystemLogger.ghiLog("Thêm thuốc " + medicineId + " vào hóa đơn " + invoiceId);
        return true;
    }

    public boolean finalizeInvoice(String invoiceId) {
        PhieuXuat invoice = invoices.get(invoiceId);
        if (invoice == null || invoiceDetails.getOrDefault(invoiceId, new ArrayList<>()).isEmpty()) {
            return false;
        }
        invoice.setPaid(true);
        saveIfConfigured();
        SystemLogger.ghiLog("Hoàn tất hóa đơn " + invoiceId);
        return true;
    }

    public PhieuXuat getInvoice(String invoiceId) {
        return invoices.get(invoiceId);
    }

    public List<ChiTietPhieuXuat> getDetails(String invoiceId) {
        return invoiceDetails.getOrDefault(invoiceId, new ArrayList<>());
    }

    public List<PhieuXuat> getAllInvoices() {
        return new ArrayList<>(invoices.values());
    }

    public void docFile(String path) throws IOException {
        Map<String, PhieuXuat> loadedInvoices = new LinkedHashMap<>();
        Map<String, List<ChiTietPhieuXuat>> loadedDetails = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                if (row.size() < 7 || ((row.size() - 7) % 5 != 0 && (row.size() - 7) % 8 != 0)) {
                    throw new IllegalArgumentException("Cấu trúc chi tiết hóa đơn không hợp lệ");
                }
                String id = row.get(0);
                if (id.isBlank() || loadedInvoices.containsKey(id)) {
                    throw new IllegalArgumentException("Mã hóa đơn rỗng hoặc bị trùng");
                }
                double total = Double.parseDouble(row.get(4));
                if (Double.isNaN(total) || Double.isInfinite(total) || total < 0) {
                    throw new IllegalArgumentException("Tổng tiền âm");
                }
                PhieuXuat invoice = new PhieuXuat(id, row.get(1), row.get(2), total, row.get(5),
                        parseBoolean(row.get(6)));
                invoice.setCreatedAt(LocalDateTime.parse(row.get(3)));
                List<ChiTietPhieuXuat> details = new ArrayList<>();
                double detailTotal = 0;
                int groupSize = row.size() == 7 ? 8 : (row.size() - 7) % 8 == 0 ? 8 : 5;
                for (int field = 7; field < row.size(); field += groupSize) {
                    int quantity;
                    double unitPrice;
                    ChiTietPhieuXuat detail;
                    if (groupSize == 8) {
                        quantity = Integer.parseInt(row.get(field + 2));
                        int saleQuantity = Integer.parseInt(row.get(field + 3));
                        unitPrice = Double.parseDouble(row.get(field + 5));
                        detail = new ChiTietPhieuXuat(row.get(field), id, row.get(field + 1),
                                quantity, unitPrice);
                        detail.setSaleDetails(saleQuantity, row.get(field + 4), row.get(field + 7));
                    } else {
                        quantity = Integer.parseInt(row.get(field + 3));
                        unitPrice = Double.parseDouble(row.get(field + 4));
                        detail = new ChiTietPhieuXuat(row.get(field), id, row.get(field + 1), quantity, unitPrice);
                    }
                    if (quantity <= 0 || Double.isNaN(unitPrice) || Double.isInfinite(unitPrice) || unitPrice < 0
                            || (groupSize == 5 && !id.equals(row.get(field + 2)))
                            || (groupSize == 8 && (Integer.parseInt(row.get(field + 3)) <= 0
                                || !id.equals(row.get(field + 6))))) {
                        throw new IllegalArgumentException("Số lượng hoặc đơn giá chi tiết không hợp lệ");
                    }
                    details.add(detail);
                    detailTotal += detail.getLineTotal();
                }
                if (Math.abs(detailTotal - total) > 0.01 || (invoice.isPaid() && details.isEmpty())) {
                    throw new IllegalArgumentException("Tổng hóa đơn không khớp chi tiết");
                }
                loadedInvoices.put(id, invoice);
                loadedDetails.put(id, details);
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("hóa đơn", i + 1, e);
            }
        }
        invoices.clear();
        invoices.putAll(loadedInvoices);
        invoiceDetails.clear();
        invoiceDetails.putAll(loadedDetails);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (PhieuXuat invoice : invoices.values()) {
            List<String> row = new ArrayList<>(java.util.Arrays.asList(invoice.getInvoiceId(),
                    invoice.getCustomerId(), invoice.getEmployeeId(), invoice.getCreatedAt().toString(),
                    Double.toString(invoice.getTotalAmount()), invoice.getPaymentMethod(),
                    Boolean.toString(invoice.isPaid())));
            for (ChiTietPhieuXuat detail : invoiceDetails.getOrDefault(invoice.getInvoiceId(), new ArrayList<>())) {
                row.add(detail.getDetailId());
                row.add(detail.getMedicineId());
                row.add(Integer.toString(detail.getQuantity()));
                row.add(Integer.toString(detail.getSaleQuantity()));
                row.add(detail.getUnitName());
                row.add(Double.toString(detail.getUnitPrice()));
                row.add(detail.getInvoiceId());
                row.add(detail.getBatchAllocations());
            }
            rows.add(row);
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ");
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu hóa đơn vào " + filePath, e);
            }
        }
    }
}
