package QuanLyKhoBai;

import QuanLyChungTu.PhieuNhap;
import QuanLyChungTu.ChiTietPhieuNhap;
import QuanLyChungTu.MaTuDong;
import QuanLyChungTu.SystemLogger;
import QuanLyChungTu.TextFileUtil;
import QuanLySanPham.DanhSachThuoc;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyNhapKho {
    private final Map<String, PhieuNhap> importNotes = new LinkedHashMap<>();
    private final DanhSachThuoc medicineManager;
    private String filePath;

    public QuanLyNhapKho(DanhSachThuoc medicineManager) {
        this.medicineManager = medicineManager;
    }

    public boolean addImportNote(PhieuNhap importNote) {
        if (importNote == null || importNote.getImportNoteId() == null || importNote.getImportNoteId().isBlank()
                || importNotes.containsKey(importNote.getImportNoteId())) {
            return false;
        }
        importNotes.put(importNote.getImportNoteId(), importNote);
        saveIfConfigured();
        SystemLogger.ghiLog("Tạo phiếu nhập " + importNote.getImportNoteId());
        return true;
    }

    public boolean importMedicine(String importNoteId, String medicineId, int quantity, double unitPrice) {
        String batchNumber = MaTuDong.taoMa("LOT");
        return importMedicine(importNoteId, medicineId, batchNumber,
                medicineManager.getMedicine(medicineId) == null
                        ? null : medicineManager.getMedicine(medicineId).getExpiryDate(),
                quantity, unitPrice);
    }

    public boolean importMedicine(String importNoteId, String medicineId, String batchNumber,
                                 LocalDate expiryDate, int quantity, double unitPrice) {
        PhieuNhap note = importNotes.get(importNoteId);
        if (note == null || quantity <= 0 || batchNumber == null || batchNumber.isBlank()
                || (medicineManager.hasBatchTracking() && expiryDate == null)
                || Double.isNaN(unitPrice) || Double.isInfinite(unitPrice) || unitPrice < 0) {
            return false;
        }
        if (medicineManager.getMedicine(medicineId) == null) {
            return false;
        }
        boolean stockIncreased = medicineManager.hasBatchTracking()
                ? medicineManager.increaseStock(medicineId, batchNumber, expiryDate, quantity)
                : medicineManager.increaseStock(medicineId, quantity);
        if (!stockIncreased) {
            return false;
        }
        note.addDetail(medicineId, batchNumber, expiryDate, quantity, unitPrice);
        note.setTotalAmount(note.getTotalAmount() + (unitPrice * quantity));
        saveIfConfigured();
        SystemLogger.ghiLog("Hoàn tất nhập thuốc " + medicineId + " theo phiếu " + importNoteId);
        return true;
    }

    public PhieuNhap getImportNote(String importNoteId) {
        return importNotes.get(importNoteId);
    }

    public List<PhieuNhap> getAllImportNotes() {
        return new ArrayList<>(importNotes.values());
    }

    public void docFile(String path) throws IOException {
        Map<String, PhieuNhap> loaded = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                if (row.size() < 5 || ((row.size() - 5) % 3 != 0 && (row.size() - 5) % 5 != 0)) {
                    throw new IllegalArgumentException("Cấu trúc chi tiết phiếu nhập không hợp lệ");
                }
                String id = row.get(0);
                double total = Double.parseDouble(row.get(4));
                if (id.isBlank() || loaded.containsKey(id) || Double.isNaN(total)
                        || Double.isInfinite(total) || total < 0) {
                    throw new IllegalArgumentException("Mã phiếu trùng/rỗng hoặc tổng tiền âm");
                }
                PhieuNhap note = new PhieuNhap(id, row.get(1), row.get(2), total);
                note.setImportDate(LocalDate.parse(row.get(3)));
                double detailTotal = 0;
                int groupSize = row.size() == 5 ? 5 : (row.size() - 5) % 5 == 0 ? 5 : 3;
                for (int field = 5; field < row.size(); field += groupSize) {
                    String batchNumber = groupSize == 5 ? row.get(field + 1) : "";
                    LocalDate expiryDate = groupSize == 5 && !row.get(field + 2).isBlank()
                            ? LocalDate.parse(row.get(field + 2)) : null;
                    int quantity = Integer.parseInt(row.get(field + (groupSize == 5 ? 3 : 1)));
                    double unitPrice = Double.parseDouble(row.get(field + (groupSize == 5 ? 4 : 2)));
                    if (quantity <= 0 || Double.isNaN(unitPrice) || Double.isInfinite(unitPrice) || unitPrice < 0) {
                        throw new IllegalArgumentException("Số lượng hoặc đơn giá nhập không hợp lệ");
                    }
                    note.addDetail(row.get(field), batchNumber, expiryDate, quantity, unitPrice);
                    detailTotal += quantity * unitPrice;
                }
                if (Math.abs(detailTotal - total) > 0.01) {
                    throw new IllegalArgumentException("Tổng tiền không khớp chi tiết phiếu nhập");
                }
                loaded.put(id, note);
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("phiếu nhập", i + 1, e);
            }
        }
        importNotes.clear();
        importNotes.putAll(loaded);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (PhieuNhap note : importNotes.values()) {
            List<String> row = new ArrayList<>(java.util.Arrays.asList(note.getImportNoteId(), note.getSupplierId(),
                    note.getEmployeeId(), note.getImportDate().toString(), Double.toString(note.getTotalAmount())));
            for (ChiTietPhieuNhap detail : note.getDetails()) {
                row.add(detail.getMedicineId());
                row.add(detail.getBatchNumber());
                row.add(detail.getExpiryDate() == null ? "" : detail.getExpiryDate().toString());
                row.add(Integer.toString(detail.getQuantity()));
                row.add(Double.toString(detail.getUnitPrice()));
            }
            rows.add(row);
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu phiếu nhập vào " + filePath, e);
            }
        }
    }
}
