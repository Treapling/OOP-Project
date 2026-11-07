package QuanLyChungTu;

public class ChiTietPhieuXuat {
    private String detailId;
    private String invoiceId;
    private String medicineId;
    private int quantity;
    private double unitPrice;

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuat(String detailId, String invoiceId, String medicineId, int quantity, double unitPrice) {
        this.detailId = detailId;
        this.invoiceId = invoiceId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getDetailId() { return detailId; }
    public void setDetailId(String detailId) { this.detailId = detailId; }
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getLineTotal() {
        return quantity * unitPrice;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuXuat{" +
                "detailId='" + detailId + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", medicineId='" + medicineId + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
