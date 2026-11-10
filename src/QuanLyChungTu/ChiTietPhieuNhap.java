package QuanLyChungTu;

import java.time.LocalDate;

public class ChiTietPhieuNhap {
    private String medicineId;
    private String batchNumber;
    private LocalDate expiryDate;
    private int quantity;
    private double unitPrice;

    public ChiTietPhieuNhap(String medicineId, int quantity, double unitPrice) {
        this(medicineId, "", null, quantity, unitPrice);
    }

    public ChiTietPhieuNhap(String medicineId, String batchNumber, LocalDate expiryDate,
                            int quantity, double unitPrice) {
        this.medicineId = medicineId;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getMedicineId() { return medicineId; }
    public String getBatchNumber() { return batchNumber; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }

    public double getLineTotal() {
        return quantity * unitPrice;
    }
}
