package QuanLySanPham;

import java.time.LocalDate;


public class Thuoc {
    private String medicineId;
    private String name;
    private String categoryId;
    private String manufacturer;
    private double unitPrice;
    private int quantityInStock;
    private LocalDate expiryDate;
    private boolean prescriptionRequired;

    public Thuoc() {
    }

    public Thuoc(String medicineId, String name, String categoryId, String manufacturer,
                    double unitPrice, int quantityInStock, LocalDate expiryDate, boolean prescriptionRequired) {
        this.medicineId = medicineId;
        this.name = name;
        this.categoryId = categoryId;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.expiryDate = expiryDate;
        this.prescriptionRequired = prescriptionRequired;
    }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }

    @Override
    public String toString() {
        return "Thuoc{" +
                "medicineId='" + medicineId + '\'' +
                ", name='" + name + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantityInStock=" + quantityInStock +
                ", expiryDate=" + expiryDate +
                ", prescriptionRequired=" + prescriptionRequired +
                '}';
    }
}
