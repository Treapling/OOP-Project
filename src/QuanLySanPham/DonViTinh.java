package QuanLySanPham;

public class DonViTinh {
    private final String name;
    private final int factorToBase;

    public DonViTinh(String name, int factorToBase) {
        this.name = name;
        this.factorToBase = factorToBase;
    }

    public String getName() { return name; }
    public int getFactorToBase() { return factorToBase; }
}
