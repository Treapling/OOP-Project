package QuanLySanPham;

import java.time.LocalDate;

public class ThuocKeDon extends SanPhamYTe {
    public ThuocKeDon() {
        super(true);
    }

    public ThuocKeDon(String medicineId, String name, String categoryId, String manufacturer,
                      double unitPrice, int quantityInStock, LocalDate expiryDate) {
        super(medicineId, name, categoryId, manufacturer, unitPrice, quantityInStock, expiryDate, true);
    }

    @Override
    public double tinhGiaBan() {
        return getUnitPrice();
    }
}
