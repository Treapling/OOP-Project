import QuanLyChungTu.DonThuoc;
import QuanLyChungTu.IThanhToan;
import QuanLyChungTu.InBaoCao;
import QuanLyChungTu.PhieuNhap;
import QuanLyChungTu.PhieuXuat;
import QuanLyChungTu.QuanLyBanHang;
import QuanLyChungTu.QuanLyDonThuoc;
import QuanLyDoiTac.BaoMatUtil;
import QuanLyDoiTac.KhachHang;
import QuanLyDoiTac.Nguoi;
import QuanLyDoiTac.NhanVienKho;
import QuanLyDoiTac.TaiKhoan;
import QuanLyKhoBai.QuanLyNhapKho;
import QuanLyPhanLoai.LoaiThuoc;
import QuanLyPhanLoai.QuanLyLoaiThuoc;
import QuanLySanPham.DanhSachThuoc;
import QuanLySanPham.SanPhamYTe;
import QuanLySanPham.ThuocKeDon;
import QuanLySanPham.ThuocKhongKeDon;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;


public class KiemThuHeThong {
    public static void main(String[] args) throws IOException {
        testCategoryManager();
        testMedicineManager();
        testInheritanceAndPayment();
        testMedicineFileIO();
        testSalesManager();
        testReportManager();
        testAuthManager();
        testImportAndPrescription();
        System.out.println("All pharmacy tests passed.");
    }

    private static void testCategoryManager() {
        QuanLyLoaiThuoc manager = new QuanLyLoaiThuoc();
        LoaiThuoc category = new LoaiThuoc("CAT-1", "Giảm đau", "Giảm đau, hạ sốt");
        assertTrue(manager.addCategory(category), "Add category failed");
        assertTrue(manager.getCategory("CAT-1") != null, "Get category failed");
        assertTrue(manager.updateCategory("CAT-1", "Kháng sinh", "Thuốc kháng sinh") && manager.getCategory("CAT-1").getName().equals("Kháng sinh"), "Update category failed");
        assertTrue(manager.deleteCategory("CAT-1"), "Delete category failed");
    }

    private static void testMedicineManager() {
        DanhSachThuoc manager = new DanhSachThuoc();
        SanPhamYTe medicine = new ThuocKhongKeDon(
                "MED-1", "Paracetamol", "CAT-1", "ABC", 12000, 10, LocalDate.now().plusDays(5));
        assertTrue(manager.addMedicine(medicine), "Add medicine failed");
        assertTrue(manager.reduceStock("MED-1", 3), "Reduce stock failed");
        assertTrue(manager.getMedicine("MED-1").getQuantityInStock() == 7, "Stock quantity incorrect");
        assertTrue(manager.filterExpiringSoon(10).size() >= 1, "Filter expiring soon failed");
    }

    private static void testInheritanceAndPayment() {
        Nguoi customer = new KhachHang("CUS-1", "Khách hàng", "0900000000", 0);
        Nguoi employee = new NhanVienKho("EMP-1", "Nhân viên", "Dược sĩ", "0911111111", 100, 0);
        assertTrue(customer.getId().equals("CUS-1") && employee.getId().equals("EMP-1"),
                "Nguoi inheritance failed");
        assertTrue(new PhieuNhap() instanceof IThanhToan, "PhieuNhap payment interface missing");
        assertTrue(new PhieuXuat() instanceof IThanhToan, "PhieuXuat payment interface missing");
    }

    private static void testMedicineFileIO() throws IOException {
        Path file = Files.createTempFile("thuoc-test-", ".csv");
        try {
            DanhSachThuoc source = new DanhSachThuoc();
            source.addMedicine(new ThuocKhongKeDon(
                    "MED-FILE-1", "Vitamin, \"C\"", "CAT-1", "ABC", 12000, 7, LocalDate.now()));
            source.addMedicine(new ThuocKeDon(
                    "MED-FILE-2", "Amoxicillin", "CAT-2", "XYZ", 45000, 3, LocalDate.now().plusDays(10)));
            source.ghiFile(file.toString());

            DanhSachThuoc loaded = new DanhSachThuoc();
            loaded.docFile(file.toString());
            assertTrue(loaded.getAllMedicines().size() == 2, "Medicine CSV round-trip size incorrect");
            assertTrue(loaded.getMedicine("MED-FILE-1").getName().equals("Vitamin, \"C\""),
                    "Medicine CSV escaping failed");
            assertTrue(loaded.getMedicine("MED-FILE-1") instanceof ThuocKhongKeDon,
                    "OTC medicine subtype was not restored");
            assertTrue(loaded.getMedicine("MED-FILE-2") instanceof ThuocKeDon,
                    "Prescription medicine subtype was not restored");
            assertTrue(loaded.getMedicine("MED-FILE-2").tinhGiaBan() == 45000,
                    "Polymorphic sale price incorrect");
        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static void testSalesManager() {
        DanhSachThuoc medicineManager = new DanhSachThuoc();
        medicineManager.addMedicine(new ThuocKeDon(
                "MED-2", "Amoxicillin", "CAT-2", "XYZ Pharma", 45000, 25, LocalDate.now().plusDays(60)));
        QuanLyBanHang salesManager = new QuanLyBanHang(medicineManager);
        salesManager.createInvoice("INV-1", "CUS-1", "EMP-1", "Cash");
        assertTrue(salesManager.addMedicineToInvoice("INV-1", "MED-2", 2), "Add medicine to invoice failed");
        assertTrue(salesManager.getInvoice("INV-1").getTotalAmount() == 90000, "PhieuXuat total incorrect");
        assertTrue(medicineManager.getMedicine("MED-2").getQuantityInStock() == 23, "Stock deducted incorrectly");
        assertTrue(salesManager.finalizeInvoice("INV-1"), "Finalize invoice failed");
    }

    private static void testReportManager() {
        PhieuXuat invoice = new PhieuXuat("INV-3", "CUS-1", "EMP-1", 250000, "Card", true);
        InBaoCao reportManager = new InBaoCao(Collections.singletonList(invoice));
        assertTrue(reportManager.getDailyRevenue(LocalDate.now()) == 250000, "Daily revenue incorrect");
    }

    private static void testAuthManager() {
        BaoMatUtil authManager = new BaoMatUtil();
        TaiKhoan account = new TaiKhoan("admin", "123456", "Admin", "EMP-1");
        assertTrue(authManager.registerAccount(account), "Register account failed");
        assertTrue(authManager.login("admin", "123456") != null, "Login failed");
        assertTrue(authManager.changePassword("admin", "123456", "654321"), "Change password failed");
        assertTrue(authManager.hasAccess("admin", "Admin"), "Access check failed");
    }

    private static void testImportAndPrescription() {
        DanhSachThuoc medicineManager = new DanhSachThuoc();
        medicineManager.addMedicine(new ThuocKhongKeDon(
                "MED-3", "Vitamin C", "CAT-3", "BioCare", 20000, 15, LocalDate.now().plusDays(120)));
        QuanLyNhapKho importManager = new QuanLyNhapKho(medicineManager);
        importManager.addImportNote(new PhieuNhap("IM-1", "SUP-1", "EMP-1", 0));
        assertTrue(importManager.importMedicine("IM-1", "MED-3", 5, 18000), "Import medicine failed");
        assertTrue(medicineManager.getMedicine("MED-3").getQuantityInStock() == 20, "Stock after import incorrect");

        QuanLyDonThuoc prescriptionManager = new QuanLyDonThuoc();
        DonThuoc prescription = new DonThuoc("PRES-1", "CUS-1", "Nguyễn Thị C", "BS. Minh", LocalDate.now());
        prescription.addMedicine("MED-3");
        assertTrue(prescriptionManager.addPrescription(prescription), "Add prescription failed");
        assertTrue(prescriptionManager.isMedicineIncluded("PRES-1", "MED-3"), "DonThuoc medicine check failed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
