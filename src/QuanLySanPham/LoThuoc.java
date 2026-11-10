package QuanLySanPham;

import java.time.LocalDate;

public class LoThuoc {
    private final String medicineId;
    private final String batchNumber;
    private final LocalDate expiryDate;
    private int quantity;
    private boolean recalled;

    public LoThuoc(String medicineId, String batchNumber, LocalDate expiryDate, int quantity) {
        this.medicineId = medicineId;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    public String getMedicineId() { return medicineId; }
    public String getBatchNumber() { return batchNumber; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isRecalled() { return recalled; }
    public void setRecalled(boolean recalled) { this.recalled = recalled; }
}
