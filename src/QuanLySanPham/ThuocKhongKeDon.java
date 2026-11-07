package QuanLySanPham;

import java.time.LocalDate;

public class ThuocKhongKeDon extends SanPhamYTe {
    public ThuocKhongKeDon() {
        super(false);
    }

    public ThuocKhongKeDon(String medicineId, String name, String categoryId, String manufacturer,
                           double unitPrice, int quantityInStock, LocalDate expiryDate) {
        super(medicineId, name, categoryId, manufacturer, unitPrice, quantityInStock, expiryDate, false);
    }

    @Override
    public double tinhGiaBan() {
        return getUnitPrice();
    }
}
