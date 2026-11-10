import QuanLyChungTu.DonThuoc;
import QuanLyChungTu.IThanhToan;
import QuanLyChungTu.InBaoCao;
import QuanLyChungTu.PhieuNhap;
import QuanLyChungTu.PhieuXuat;
import QuanLyChungTu.QuanLyBanHang;
import QuanLyChungTu.QuanLyDonThuoc;
import QuanLyDoiTac.BaoMatUtil;
import QuanLyDoiTac.KhachHang;
import QuanLyDoiTac.NhaCungCap;
import QuanLyDoiTac.Nguoi;
import QuanLyDoiTac.NhanVienKho;
import QuanLyDoiTac.QuanLyKhachHang;
import QuanLyDoiTac.QuanLyNhaCungCap;
import QuanLyDoiTac.QuanLyNhanVienKho;
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
import java.util.HashMap;
import java.util.Map;


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
        testPersistenceForAllManagers();
        testSeedDataAndPharmacyScenario();
        testBatchRecall();
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
                    "MED-FILE-1", "Vitamin, \"C\"\t500mg\nchai", "CAT-1", "ABC",
                    12000, 7, LocalDate.now()));
            source.addMedicine(new ThuocKeDon(
                    "MED-FILE-2", "Amoxicillin", "CAT-2", "XYZ", 45000, 3, LocalDate.now().plusDays(10)));
            source.ghiFile(file.toString());

            DanhSachThuoc loaded = new DanhSachThuoc();
            loaded.docFile(file.toString());
            assertTrue(loaded.getAllMedicines().size() == 2, "Medicine CSV round-trip size incorrect");
            assertTrue(loaded.getMedicine("MED-FILE-1").getName().equals("Vitamin, \"C\"\t500mg\nchai"),
                    "Medicine text escaping failed");
            assertTrue(loaded.getMedicine("MED-FILE-1") instanceof ThuocKhongKeDon,
                    "OTC medicine subtype was not restored");
            assertTrue(loaded.getMedicine("MED-FILE-2") instanceof ThuocKeDon,
                    "Prescription medicine subtype was not restored");
            assertTrue(loaded.getMedicine("MED-FILE-2").tinhGiaBan() == 47250,
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
        assertTrue(salesManager.getInvoice("INV-1").getTotalAmount() == 94500, "PhieuXuat total incorrect");
        assertTrue(medicineManager.getMedicine("MED-2").getQuantityInStock() == 23, "Stock deducted incorrectly");
        assertTrue(salesManager.finalizeInvoice("INV-1"), "Finalize invoice failed");
    }

    private static void testReportManager() {
        PhieuXuat invoice = new PhieuXuat("INV-3", "CUS-1", "EMP-1", 250000, "Card", true);
        PhieuXuat unpaid = new PhieuXuat("INV-UNPAID", "CUS-1", "EMP-1", 90000, "Cash", false);
        InBaoCao reportManager = new InBaoCao(java.util.Arrays.asList(invoice, unpaid));
        assertTrue(reportManager.getDailyRevenue(LocalDate.now()) == 250000, "Daily revenue incorrect");
        assertTrue(reportManager.getInvoiceCount(LocalDate.now()) == 1, "Paid invoice count incorrect");
    }

    private static void testAuthManager() {
        BaoMatUtil authManager = new BaoMatUtil();
        TaiKhoan account = new TaiKhoan("admin", "123456", "Admin", "EMP-1");
        assertTrue(authManager.registerAccount(account), "Register account failed");
        assertTrue(!account.checkPassword(null), "Null password should not match");
        assertTrue(authManager.login("admin", "123456") != null, "Login failed");
        assertTrue(authManager.login("admin", "wrong") == null, "Wrong password was accepted");
        assertTrue(authManager.changePassword("admin", "123456", "654321"), "Change password failed");
        assertTrue(account.checkPassword("654321") && !account.checkPassword("123456"),
                "Changed password was not applied");
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

    private static void testPersistenceForAllManagers() throws IOException {
        Path directory = Files.createTempDirectory("pharmacy-persistence-");
        try {
            Path categoryPath = directory.resolve("categories.txt");
            QuanLyLoaiThuoc categories = new QuanLyLoaiThuoc();
            categories.ghiFile(categoryPath.toString());
            assertTrue(categories.addCategory(new LoaiThuoc("CAT-P", "Test", "Mô tả")),
                    "Category auto-save add failed");
            assertTrue(Files.readAllLines(categoryPath).size() == 1, "Category add was not written");
            assertTrue(categories.updateCategory("CAT-P", "Updated", "Changed"),
                    "Category auto-save update failed");
            QuanLyLoaiThuoc loadedCategories = new QuanLyLoaiThuoc();
            loadedCategories.docFile(categoryPath.toString());
            assertTrue("Updated".equals(loadedCategories.getCategory("CAT-P").getName()),
                    "Category update did not survive reload");
            assertTrue(loadedCategories.deleteCategory("CAT-P"), "Category auto-save delete failed");
            assertTrue(Files.readAllLines(categoryPath).isEmpty(), "Category delete was not written");

            Path medicinePath = directory.resolve("medicines.txt");
            DanhSachThuoc medicines = new DanhSachThuoc();
            medicines.ghiFile(medicinePath.toString());
            SanPhamYTe medicine = new ThuocKhongKeDon(
                    "MED-P", "Paracetamol", "CAT-P", "Nhà máy A", 10000, 12, LocalDate.now().plusDays(30));
            assertTrue(medicines.addMedicine(medicine), "Medicine auto-save add failed");
            medicine.setUnitPrice(11000);
            assertTrue(medicines.updateMedicine(medicine), "Medicine auto-save update failed");
            assertTrue(medicines.reduceStock("MED-P", 2), "Medicine stock auto-save failed");
            DanhSachThuoc loadedMedicines = new DanhSachThuoc();
            loadedMedicines.docFile(medicinePath.toString());
            assertTrue(loadedMedicines.getMedicine("MED-P").getQuantityInStock() == 10
                    && loadedMedicines.getMedicine("MED-P").getUnitPrice() == 11000,
                    "Medicine changes did not survive reload");
            assertTrue(loadedMedicines.deleteMedicine("MED-P"), "Medicine auto-save delete failed");
            assertTrue(Files.readAllLines(medicinePath).isEmpty(), "Medicine delete was not written");

            Path customerPath = directory.resolve("customers.txt");
            QuanLyKhachHang customers = new QuanLyKhachHang();
            customers.ghiFile(customerPath.toString());
            assertTrue(customers.addCustomer(new KhachHang("CUS-P", "Khách thử", "0900", 0)),
                    "Customer auto-save add failed");
            KhachHang customer = customers.getCustomer("CUS-P");
            customer.setName("Khách cập nhật");
            assertTrue(customers.updateCustomer(customer), "Customer auto-save update failed");
            customers.addPoints("CUS-P", 500);
            QuanLyKhachHang loadedCustomers = new QuanLyKhachHang();
            loadedCustomers.docFile(customerPath.toString());
            assertTrue(loadedCustomers.getCustomer("CUS-P").getLoyaltyPoints() == 500
                    && "Khách cập nhật".equals(loadedCustomers.getCustomer("CUS-P").getName()),
                    "Customer changes did not survive reload");
            assertTrue(loadedCustomers.deleteCustomer("CUS-P"), "Customer auto-save delete failed");
            assertTrue(Files.readAllLines(customerPath).isEmpty(), "Customer delete was not written");

            Path employeePath = directory.resolve("employees.txt");
            QuanLyNhanVienKho employees = new QuanLyNhanVienKho();
            employees.ghiFile(employeePath.toString());
            assertTrue(employees.addEmployee(new NhanVienKho("EMP-P", "Nhân viên", "Dược sĩ",
                    "0911", 9000000, 0)), "Employee auto-save add failed");
            assertTrue(employees.updateEmployee(new NhanVienKho("EMP-P", "Nhân viên mới", "Dược sĩ",
                    "0912", 10000000, 100000)), "Employee auto-save update failed");
            QuanLyNhanVienKho loadedEmployees = new QuanLyNhanVienKho();
            loadedEmployees.docFile(employeePath.toString());
            assertTrue(loadedEmployees.calculateSalary("EMP-P") == 10100000,
                    "Employee changes did not survive reload");
            assertTrue(loadedEmployees.deleteEmployee("EMP-P"), "Employee auto-save delete failed");
            assertTrue(Files.readAllLines(employeePath).isEmpty(), "Employee delete was not written");

            Path supplierPath = directory.resolve("suppliers.txt");
            QuanLyNhaCungCap suppliers = new QuanLyNhaCungCap();
            suppliers.ghiFile(supplierPath.toString());
            assertTrue(suppliers.addSupplier(new NhaCungCap("SUP-P", "Nhà cung cấp", "0900", "Địa chỉ")),
                    "Supplier auto-save add failed");
            assertTrue(suppliers.updateSupplier(new NhaCungCap("SUP-P", "NCC mới", "0901", "Địa chỉ mới")),
                    "Supplier auto-save update failed");
            QuanLyNhaCungCap loadedSuppliers = new QuanLyNhaCungCap();
            loadedSuppliers.docFile(supplierPath.toString());
            assertTrue("NCC mới".equals(loadedSuppliers.getSupplier("SUP-P").getName()),
                    "Supplier changes did not survive reload");
            assertTrue(loadedSuppliers.deleteSupplier("SUP-P"), "Supplier auto-save delete failed");
            assertTrue(Files.readAllLines(supplierPath).isEmpty(), "Supplier delete was not written");

            Path invoicePath = directory.resolve("invoices.txt");
            Path stockPath = directory.resolve("stock.txt");
            DanhSachThuoc saleMedicines = new DanhSachThuoc();
            saleMedicines.ghiFile(stockPath.toString());
            saleMedicines.addMedicine(new ThuocKhongKeDon(
                    "MED-S", "Dung dịch", "CAT-P", "Nhà máy B", 5000, 10, LocalDate.now().plusDays(30)));
            QuanLyBanHang sales = new QuanLyBanHang(saleMedicines);
            sales.ghiFile(invoicePath.toString());
            assertTrue(sales.createInvoice("INV-P", "CUS-P", "EMP-P", "Tiền mặt") != null,
                    "Invoice auto-save create failed");
            assertTrue(Files.readAllLines(invoicePath).size() == 1, "Invoice create was not written");
            assertTrue(sales.addMedicineToInvoice("INV-P", "MED-S", 2), "Invoice item add failed");
            assertTrue(sales.finalizeInvoice("INV-P"), "Invoice finalize failed");
            DanhSachThuoc reloadedStock = new DanhSachThuoc();
            reloadedStock.docFile(stockPath.toString());
            QuanLyBanHang loadedSales = new QuanLyBanHang(reloadedStock);
            loadedSales.docFile(invoicePath.toString());
            assertTrue(reloadedStock.getMedicine("MED-S").getQuantityInStock() == 8,
                    "Sale stock deduction not saved");
            assertTrue(loadedSales.getInvoice("INV-P").isPaid()
                    && loadedSales.getInvoice("INV-P").getTotalAmount() == 10000
                    && loadedSales.getDetails("INV-P").size() == 1,
                    "Invoice total/details/status did not survive reload");
            assertTrue(!loadedSales.addMedicineToInvoice("INV-P", "MED-S", 1),
                    "Paid invoice accepted another item");

            Path importPath = directory.resolve("imports.txt");
            QuanLyNhapKho imports = new QuanLyNhapKho(saleMedicines);
            imports.ghiFile(importPath.toString());
            assertTrue(imports.addImportNote(new PhieuNhap("PN-P", "SUP-P", "EMP-P", 0)),
                    "Import note auto-save create failed");
            assertTrue(imports.importMedicine("PN-P", "MED-S", 3, 4000), "Stock import failed");
            QuanLyNhapKho loadedImports = new QuanLyNhapKho(reloadedStock);
            loadedImports.docFile(importPath.toString());
            DanhSachThuoc stockAfterImport = new DanhSachThuoc();
            stockAfterImport.docFile(stockPath.toString());
            assertTrue(loadedImports.getImportNote("PN-P").getTotalAmount() == 12000
                    && loadedImports.getImportNote("PN-P").getMedicineIds().contains("MED-S"),
                    "Import data did not survive reload");
            assertTrue(stockAfterImport.getMedicine("MED-S").getQuantityInStock() == 11,
                    "Imported stock was not saved to the medicine file");

            Path prescriptionPath = directory.resolve("prescriptions.txt");
            QuanLyDonThuoc prescriptions = new QuanLyDonThuoc();
            prescriptions.ghiFile(prescriptionPath.toString());
            DonThuoc prescription = new DonThuoc(
                    "PRES-P", "CUS-P", "Bệnh nhân", "BS. Test", LocalDate.now());
            prescription.addMedicine("MED-S");
            assertTrue(prescriptions.addPrescription(prescription), "Prescription auto-save create failed");
            QuanLyDonThuoc loadedPrescriptions = new QuanLyDonThuoc();
            loadedPrescriptions.docFile(prescriptionPath.toString());
            assertTrue(loadedPrescriptions.isMedicineIncluded("PRES-P", "MED-S"),
                    "Prescription did not survive reload");

            saleMedicines.addMedicine(new ThuocKhongKeDon(
                    "MED-X", "Thuốc hết hạn", "CAT-P", "Nhà máy B", 5000, 10, LocalDate.now().minusDays(1)));
            assertTrue(sales.createInvoice("INV-X", "CUS-P", "EMP-P", "Tiền mặt") != null,
                    "Expired-product test invoice was not created");
            assertTrue(!sales.addMedicineToInvoice("INV-X", "MED-X", 1),
                    "Expired medicine was allowed to sell");
            assertTrue(!sales.finalizeInvoice("INV-X"), "Empty invoice was finalized");
        } finally {
            try (java.util.stream.Stream<Path> paths = Files.walk(directory)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toArray(Path[]::new)) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static void testSeedDataAndPharmacyScenario() throws IOException {
        Path directory = Files.createTempDirectory("pharmacy-seed-");
        String[] fileNames = {"thuoc.txt", "lo-thuoc.txt", "loai-thuoc.txt", "khach-hang.txt", "nhan-vien.txt",
                "nha-cung-cap.txt", "hoa-don.txt", "phieu-nhap.txt", "toa-thuoc.txt"};
        try {
            for (String fileName : fileNames) {
                Path source = Path.of("data", fileName);
                assertTrue(Files.readAllLines(source).size() == 5, "Expected five sample rows in " + fileName);
                Files.copy(source, directory.resolve(fileName));
            }
            QuanLyLoaiThuoc categories = new QuanLyLoaiThuoc();
            categories.docFile(directory.resolve("loai-thuoc.txt").toString());
            DanhSachThuoc medicines = new DanhSachThuoc();
            medicines.docFile(directory.resolve("thuoc.txt").toString());
            medicines.docBatchFile(directory.resolve("lo-thuoc.txt").toString());
            QuanLyKhachHang customers = new QuanLyKhachHang();
            customers.docFile(directory.resolve("khach-hang.txt").toString());
            QuanLyNhanVienKho employees = new QuanLyNhanVienKho();
            employees.docFile(directory.resolve("nhan-vien.txt").toString());
            QuanLyNhaCungCap suppliers = new QuanLyNhaCungCap();
            suppliers.docFile(directory.resolve("nha-cung-cap.txt").toString());
            QuanLyBanHang sales = new QuanLyBanHang(medicines);
            sales.docFile(directory.resolve("hoa-don.txt").toString());
            QuanLyNhapKho imports = new QuanLyNhapKho(medicines);
            imports.docFile(directory.resolve("phieu-nhap.txt").toString());
            QuanLyDonThuoc prescriptions = new QuanLyDonThuoc();
            prescriptions.docFile(directory.resolve("toa-thuoc.txt").toString());

            assertTrue(categories.getAllCategories().size() == 5 && medicines.getAllMedicines().size() == 5
                    && customers.getAllCustomers().size() == 5 && employees.getAllEmployees().size() == 5
                    && suppliers.getAllSuppliers().size() == 5 && sales.getAllInvoices().size() == 5
                    && imports.getAllImportNotes().size() == 5
                    && prescriptions.getAllPrescriptions().size() == 5,
                    "One or more seeded data files did not load");
            assertTrue(medicines.getLowStockMedicines().stream()
                    .anyMatch(medicine -> medicine.getMedicineId().equals("MED-002")),
                    "Seed data should demonstrate a reorder warning");
            for (SanPhamYTe medicine : medicines.getAllMedicines()) {
                assertTrue(categories.getCategory(medicine.getCategoryId()) != null,
                        "Sample medicine refers to a missing category");
            }

            assertTrue(medicines.getMedicine("MED-002").isPrescriptionRequired()
                    && prescriptions.isMedicineIncluded("PRES-001", "MED-002"),
                    "Prescription data is not linked to the prescription medicine");
            for (PhieuXuat invoice : sales.getAllInvoices()) {
                assertTrue(customers.getCustomer(invoice.getCustomerId()) != null
                        && employees.getEmployee(invoice.getEmployeeId()) != null,
                        "Sample invoice refers to an unknown customer or employee");
                double detailTotal = sales.getDetails(invoice.getInvoiceId()).stream()
                        .mapToDouble(detail -> detail.getLineTotal()).sum();
                assertTrue(Math.abs(detailTotal - invoice.getTotalAmount()) < 0.01,
                        "Sample invoice total does not reconcile with its details");
            }
            for (PhieuNhap note : imports.getAllImportNotes()) {
                assertTrue(suppliers.getSupplier(note.getSupplierId()) != null
                        && employees.getEmployee(note.getEmployeeId()) != null,
                        "Sample import refers to an unknown supplier or employee");
            }
            for (DonThuoc prescription : prescriptions.getAllPrescriptions()) {
                assertTrue(customers.getCustomer(prescription.getCustomerId()) != null
                        && prescription.getMedicineIds().stream().allMatch(id -> medicines.getMedicine(id) != null),
                        "Sample prescription refers to an unknown customer or medicine");
            }
            Map<String, Integer> expectedStock = new HashMap<>();
            for (PhieuNhap note : imports.getAllImportNotes()) {
                for (QuanLyChungTu.ChiTietPhieuNhap detail : note.getDetails()) {
                    expectedStock.merge(detail.getMedicineId(), detail.getQuantity(), Integer::sum);
                }
            }
            for (PhieuXuat invoice : sales.getAllInvoices()) {
                for (QuanLyChungTu.ChiTietPhieuXuat detail : sales.getDetails(invoice.getInvoiceId())) {
                    expectedStock.merge(detail.getMedicineId(), -detail.getQuantity(), Integer::sum);
                }
            }
            for (SanPhamYTe medicine : medicines.getAllMedicines()) {
                assertTrue(expectedStock.getOrDefault(medicine.getMedicineId(), 0) == medicine.getQuantityInStock(),
                        "Sample stock does not reconcile with imports and sales for " + medicine.getMedicineId());
            }
            SanPhamYTe paracetamol = medicines.getMedicine("MED-001");
            assertTrue(paracetamol.getSellUnits().get(2).getName().equals("hộp")
                    && paracetamol.getSellUnits().get(2).getFactorToBase() == 100,
                    "Per-product box-to-tablet conversion was not loaded");
            paracetamol.setReorderLevel(2000);
            assertTrue(medicines.getLowStockMedicines().contains(paracetamol),
                    "Minimum stock warning did not detect a below-threshold product");
            assertTrue(medicines.searchMedicines("paraceta", "CAT-001", "Duoc Hau", null, true)
                    .contains(paracetamol), "Combined fuzzy product search failed");
            paracetamol.setReorderLevel(100);

            LocalDate urgentExpiry = LocalDate.now().plusDays(20);
            assertTrue(imports.importMedicine("PN-001", "MED-001", "LOT-URGENT", urgentExpiry, 5, 300),
                    "Near-expiry stock lot import failed");

            assertTrue(sales.createInvoice("HD-TEST", "CUS-001", "EMP-001", "Tiền mặt") != null,
                    "Realistic sale scenario could not create an invoice");
            int previousStock = medicines.getMedicine("MED-001").getQuantityInStock();
            assertTrue(sales.addMedicineToInvoice("HD-TEST", "MED-001", 2), "Realistic sale failed");
            assertTrue(sales.getInvoice("HD-TEST").getTotalAmount() == 1000
                    && medicines.getMedicine("MED-001").getQuantityInStock() == previousStock - 2,
                    "Sale amount/stock reconciliation failed");
            assertTrue(sales.getDetails("HD-TEST").get(0).getBatchAllocations().equals("LOT-URGENT:2"),
                    "Sale did not apply FEFO to the nearest expiry batch");
            assertTrue(sales.finalizeInvoice("HD-TEST"), "Realistic sale could not be finalized");
            assertTrue(sales.createInvoice("HD-PACK", "CUS-001", "EMP-001", "Tiền mặt") != null
                    && sales.addMedicineToInvoice("HD-PACK", "MED-001", 1, "hộp")
                    && sales.getInvoice("HD-PACK").getTotalAmount() == 50000
                    && sales.getDetails("HD-PACK").get(0).getQuantity() == 100,
                    "Selling a box did not convert to 100 tablets correctly");
            assertTrue(sales.finalizeInvoice("HD-PACK"), "Converted-unit sale could not be finalized");
            DanhSachThuoc persistedMedicines = new DanhSachThuoc();
            persistedMedicines.docFile(directory.resolve("thuoc.txt").toString());
            persistedMedicines.docBatchFile(directory.resolve("lo-thuoc.txt").toString());
            QuanLyBanHang persistedSales = new QuanLyBanHang(persistedMedicines);
            persistedSales.docFile(directory.resolve("hoa-don.txt").toString());
            assertTrue(persistedMedicines.getMedicine("MED-001").getQuantityInStock() == previousStock - 102
                    && persistedSales.getInvoice("HD-TEST").isPaid()
                    && persistedSales.getDetails("HD-TEST").get(0).getLineTotal() == 1000
                    && persistedSales.getDetails("HD-TEST").get(0).getBatchAllocations()
                        .contains("LOT-URGENT:2")
                    && persistedSales.getDetails("HD-PACK").get(0).getSaleQuantity() == 1
                    && persistedSales.getDetails("HD-PACK").get(0).getUnitName().equals("hộp"),
                    "Sale files do not reconcile after reloading");

            assertTrue(imports.importMedicine("PN-001", "MED-001", 5, 10000), "Realistic stock receipt failed");
            DanhSachThuoc afterImportMedicines = new DanhSachThuoc();
            afterImportMedicines.docFile(directory.resolve("thuoc.txt").toString());
            afterImportMedicines.docBatchFile(directory.resolve("lo-thuoc.txt").toString());
            QuanLyNhapKho persistedImports = new QuanLyNhapKho(persistedMedicines);
            persistedImports.docFile(directory.resolve("phieu-nhap.txt").toString());
            assertTrue(afterImportMedicines.getMedicine("MED-001").getQuantityInStock() == previousStock - 97
                    && persistedImports.getImportNote("PN-001").getTotalAmount() == 351500
                    && afterImportMedicines.getBatches("MED-001").size() == 3,
                    "Import receipt and stock files do not reconcile");
        } finally {
            try (java.util.stream.Stream<Path> paths = Files.walk(directory)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toArray(Path[]::new)) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static void testBatchRecall() throws IOException {
        Path directory = Files.createTempDirectory("pharmacy-recall-");
        try {
            Path productFile = directory.resolve("products.txt");
            Path batchFile = directory.resolve("batches.txt");
            DanhSachThuoc medicines = new DanhSachThuoc();
            medicines.ghiFile(productFile.toString());
            assertTrue(medicines.addMedicine(new ThuocKhongKeDon(
                    "MED-R", "Vật tư thử", "CAT-R", "Nhà máy", 100, 5,
                    LocalDate.now().plusDays(100))), "Recall fixture product failed");
            medicines.taoLoBanDau(batchFile.toString());
            assertTrue(medicines.recallBatch("INIT-MED-R"), "Batch recall operation failed");
            assertTrue(medicines.getAvailableStock("MED-R") == 0
                    && medicines.getMedicine("MED-R").getQuantityInStock() == 5,
                    "Recalled inventory should be quarantined but remain physically counted");
            QuanLyBanHang sales = new QuanLyBanHang(medicines);
            assertTrue(sales.createInvoice("INV-R", "CUS-R", "EMP-R", "Cash") != null
                    && !sales.addMedicineToInvoice("INV-R", "MED-R", 1),
                    "Recalled batch was available for sale");
            DanhSachThuoc reloaded = new DanhSachThuoc();
            reloaded.docFile(productFile.toString());
            reloaded.docBatchFile(batchFile.toString());
            assertTrue(reloaded.getBatches("MED-R").get(0).isRecalled()
                    && reloaded.getAvailableStock("MED-R") == 0,
                    "Recalled status did not persist across reload");
        } finally {
            try (java.util.stream.Stream<Path> paths = Files.walk(directory)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toArray(Path[]::new)) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
