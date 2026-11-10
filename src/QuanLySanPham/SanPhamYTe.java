package QuanLySanPham;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class SanPhamYTe {
    private String medicineId;
    private String name;
    private String categoryId;
    private String manufacturer;
    private double unitPrice;
    private int quantityInStock;
    private LocalDate expiryDate;
    private int reorderLevel;
    private String baseUnitName = "đơn vị";
    private String middleUnitName = "";
    private int middleToBaseFactor = 1;
    private String outerUnitName = "";
    private int outerToMiddleFactor = 1;
    private final boolean prescriptionRequired;

    protected SanPhamYTe(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    protected SanPhamYTe(String medicineId, String name, String categoryId, String manufacturer,
                         double unitPrice, int quantityInStock, LocalDate expiryDate,
                         boolean prescriptionRequired) {
        this.medicineId = medicineId;
        this.name = name;
        this.categoryId = categoryId;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.expiryDate = expiryDate;
        this.prescriptionRequired = prescriptionRequired;
    }

    public abstract double tinhGiaBan();

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
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public String getBaseUnitName() { return baseUnitName; }
    public String getMiddleUnitName() { return middleUnitName; }
    public int getMiddleToBaseFactor() { return middleToBaseFactor; }
    public String getOuterUnitName() { return outerUnitName; }
    public int getOuterToMiddleFactor() { return outerToMiddleFactor; }
    public void setUnitConfiguration(String baseUnitName, String middleUnitName, int middleToBaseFactor,
                                     String outerUnitName, int outerToMiddleFactor) {
        if (baseUnitName == null || baseUnitName.isBlank()
                || middleToBaseFactor < 1 || outerToMiddleFactor < 1
                || (middleUnitName != null && !middleUnitName.isBlank() && middleToBaseFactor <= 1)
                || (outerUnitName != null && !outerUnitName.isBlank()
                    && (middleUnitName == null || middleUnitName.isBlank() || outerToMiddleFactor <= 1))
                || (middleUnitName != null && baseUnitName.equalsIgnoreCase(middleUnitName))
                || (outerUnitName != null && !outerUnitName.isBlank()
                    && (baseUnitName.equalsIgnoreCase(outerUnitName) || middleUnitName.equalsIgnoreCase(outerUnitName)))) {
            throw new IllegalArgumentException("Quy cách đơn vị không hợp lệ");
        }
        if (outerUnitName != null && !outerUnitName.isBlank()) {
            try {
                Math.multiplyExact(middleToBaseFactor, outerToMiddleFactor);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Hệ số quy đổi vượt giới hạn", e);
            }
        }
        this.baseUnitName = baseUnitName;
        this.middleUnitName = middleUnitName == null ? "" : middleUnitName;
        this.middleToBaseFactor = middleToBaseFactor;
        this.outerUnitName = outerUnitName == null ? "" : outerUnitName;
        this.outerToMiddleFactor = outerToMiddleFactor;
    }

    public List<DonViTinh> getSellUnits() {
        List<DonViTinh> units = new ArrayList<>();
        units.add(new DonViTinh(baseUnitName, 1));
        if (!middleUnitName.isBlank()) {
            units.add(new DonViTinh(middleUnitName, middleToBaseFactor));
        }
        if (!outerUnitName.isBlank()) {
            units.add(new DonViTinh(outerUnitName, Math.multiplyExact(middleToBaseFactor, outerToMiddleFactor)));
        }
        return units;
    }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }

    @Override
    public String toString() {
        return "SanPhamYTe{" +
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
