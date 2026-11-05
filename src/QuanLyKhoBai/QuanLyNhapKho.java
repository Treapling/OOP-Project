package QuanLyKhoBai;

import QuanLyChungTu.PhieuNhap;
import QuanLyChungTu.SystemLogger;
import QuanLySanPham.DanhSachThuoc;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyNhapKho {
    private final Map<String, PhieuNhap> importNotes = new LinkedHashMap<>();
    private final DanhSachThuoc medicineManager;

    public QuanLyNhapKho(DanhSachThuoc medicineManager) {
        this.medicineManager = medicineManager;
    }

    public boolean addImportNote(PhieuNhap importNote) {
        if (importNote == null || importNote.getImportNoteId() == null || importNote.getImportNoteId().isBlank()) {
            return false;
        }
        importNotes.put(importNote.getImportNoteId(), importNote);
        SystemLogger.ghiLog("Tạo phiếu nhập " + importNote.getImportNoteId());
        return true;
    }

    public boolean importMedicine(String importNoteId, String medicineId, int quantity, double unitPrice) {
        PhieuNhap note = importNotes.get(importNoteId);
        if (note == null || quantity <= 0 || unitPrice < 0) {
            return false;
        }
        if (medicineManager.getMedicine(medicineId) == null) {
            return false;
        }
        if (!medicineManager.increaseStock(medicineId, quantity)) {
            return false;
        }
        note.addMedicine(medicineId);
        note.setTotalAmount(note.getTotalAmount() + (unitPrice * quantity));
        SystemLogger.ghiLog("Hoàn tất nhập thuốc " + medicineId + " theo phiếu " + importNoteId);
        return true;
    }

    public PhieuNhap getImportNote(String importNoteId) {
        return importNotes.get(importNoteId);
    }

    public List<PhieuNhap> getAllImportNotes() {
        return new ArrayList<>(importNotes.values());
    }
}
