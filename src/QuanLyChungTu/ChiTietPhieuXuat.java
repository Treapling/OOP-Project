package QuanLyChungTu;

public class ChiTietPhieuXuat {
    private String detailId;
    private String invoiceId;
    private String medicineId;
    private int quantity;
    private double unitPrice;
    private int saleQuantity;
    private String unitName = "đơn vị";
    private String batchAllocations = "";

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuat(String detailId, String invoiceId, String medicineId, int quantity, double unitPrice) {
        this.detailId = detailId;
        this.invoiceId = invoiceId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.saleQuantity = quantity;
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
    public int getSaleQuantity() { return saleQuantity; }
    public String getUnitName() { return unitName; }
    public String getBatchAllocations() { return batchAllocations; }

    public void setSaleDetails(int saleQuantity, String unitName, String batchAllocations) {
        this.saleQuantity = saleQuantity;
        this.unitName = unitName;
        this.batchAllocations = batchAllocations == null ? "" : batchAllocations;
    }

    public double getLineTotal() {
        return saleQuantity * unitPrice;
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
