package QuanLyChungTu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PhieuNhap implements IThanhToan {
    private String importNoteId;
    private String supplierId;
    private String employeeId;
    private LocalDate importDate;
    private double totalAmount;
    private List<String> medicineIds = new ArrayList<>();
    private List<ChiTietPhieuNhap> details = new ArrayList<>();

    public PhieuNhap() {
        this.importDate = LocalDate.now();
    }

    public PhieuNhap(String importNoteId, String supplierId, String employeeId, double totalAmount) {
        this.importNoteId = importNoteId;
        this.supplierId = supplierId;
        this.employeeId = employeeId;
        this.importDate = LocalDate.now();
        this.totalAmount = totalAmount;
    }

    public String getImportNoteId() { return importNoteId; }
    public void setImportNoteId(String importNoteId) { this.importNoteId = importNoteId; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public LocalDate getImportDate() { return importDate; }
    public void setImportDate(LocalDate importDate) { this.importDate = importDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public List<String> getMedicineIds() { return medicineIds; }
    public void setMedicineIds(List<String> medicineIds) { this.medicineIds = medicineIds; }
    public List<ChiTietPhieuNhap> getDetails() { return details; }
    public void setDetails(List<ChiTietPhieuNhap> details) { this.details = details; }

    public void addMedicine(String medicineId) {
        if (!medicineIds.contains(medicineId)) {
            medicineIds.add(medicineId);
        }
    }

    public void addDetail(String medicineId, int quantity, double unitPrice) {
        addDetail(medicineId, "", null, quantity, unitPrice);
    }

    public void addDetail(String medicineId, String batchNumber, LocalDate expiryDate,
                          int quantity, double unitPrice) {
        addMedicine(medicineId);
        details.add(new ChiTietPhieuNhap(medicineId, batchNumber, expiryDate, quantity, unitPrice));
    }

    @Override
    public void xuatHoaDon() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "PhieuNhap{" +
                "importNoteId='" + importNoteId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", importDate=" + importDate +
                ", totalAmount=" + totalAmount +
                ", medicineIds=" + medicineIds +
                '}';
    }
}
