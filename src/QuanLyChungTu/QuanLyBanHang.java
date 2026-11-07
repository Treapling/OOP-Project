package QuanLyChungTu;

import QuanLySanPham.DanhSachThuoc;
import QuanLySanPham.SanPhamYTe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyBanHang {
    private final Map<String, PhieuXuat> invoices = new LinkedHashMap<>();
    private final Map<String, List<ChiTietPhieuXuat>> invoiceDetails = new LinkedHashMap<>();
    private final DanhSachThuoc medicineManager;

    public QuanLyBanHang(DanhSachThuoc medicineManager) {
        this.medicineManager = medicineManager;
    }

    public PhieuXuat createInvoice(String invoiceId, String customerId, String employeeId, String paymentMethod) {
        PhieuXuat invoice = new PhieuXuat(invoiceId, customerId, employeeId, 0, paymentMethod, false);
        invoices.put(invoiceId, invoice);
        invoiceDetails.put(invoiceId, new ArrayList<>());
        SystemLogger.ghiLog("Tạo hóa đơn " + invoiceId);
        return invoice;
    }

    public boolean addMedicineToInvoice(String invoiceId, String medicineId, int quantity) {
        PhieuXuat invoice = invoices.get(invoiceId);
        List<ChiTietPhieuXuat> detailList = invoiceDetails.get(invoiceId);
        if (invoice == null || detailList == null) {
            return false;
        }
        SanPhamYTe medicine = medicineManager.getMedicine(medicineId);
        if (medicine == null || quantity <= 0 || medicine.getQuantityInStock() < quantity) {
            return false;
        }

        if (!medicineManager.reduceStock(medicineId, quantity)) {
            return false;
        }

        ChiTietPhieuXuat detail = new ChiTietPhieuXuat(
                invoiceId + "-" + (detailList.size() + 1),
                invoiceId,
                medicineId,
                quantity,
                medicine.tinhGiaBan()
        );
        detailList.add(detail);
        invoice.setTotalAmount(invoice.getTotalAmount() + detail.getLineTotal());
        SystemLogger.ghiLog("Thêm thuốc " + medicineId + " vào hóa đơn " + invoiceId);
        return true;
    }

    public boolean finalizeInvoice(String invoiceId) {
        PhieuXuat invoice = invoices.get(invoiceId);
        if (invoice == null) {
            return false;
        }
        invoice.setPaid(true);
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

    public List<ChiTietPhieuXuat> getAllDetails() {
        List<ChiTietPhieuXuat> allDetails = new ArrayList<>();
        for (List<ChiTietPhieuXuat> details : invoiceDetails.values()) {
            allDetails.addAll(details);
        }
        return allDetails;
    }

    public void replaceAllInvoices(List<PhieuXuat> loadedInvoices,
                                   List<ChiTietPhieuXuat> loadedDetails) {
        invoices.clear();
        invoiceDetails.clear();
        for (PhieuXuat invoice : loadedInvoices) {
            invoices.put(invoice.getInvoiceId(), invoice);
            invoiceDetails.put(invoice.getInvoiceId(), new ArrayList<>());
        }
        for (ChiTietPhieuXuat detail : loadedDetails) {
            List<ChiTietPhieuXuat> details = invoiceDetails.get(detail.getInvoiceId());
            if (details != null) {
                details.add(detail);
            }
        }
    }
}
