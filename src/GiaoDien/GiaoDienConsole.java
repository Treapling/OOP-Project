package GiaoDien;

import QuanLyChungTu.DonThuoc;
import QuanLyChungTu.InBaoCao;
import QuanLyChungTu.PhieuNhap;
import QuanLyChungTu.PhieuXuat;
import QuanLyChungTu.QuanLyBanHang;
import QuanLyChungTu.QuanLyDonThuoc;
import QuanLyChungTu.LuuTruDuLieu;
import QuanLyDoiTac.KhachHang;
import QuanLyDoiTac.NhaCungCap;
import QuanLyDoiTac.NhanVienKho;
import QuanLyDoiTac.QuanLyKhachHang;
import QuanLyDoiTac.QuanLyNhaCungCap;
import QuanLyDoiTac.QuanLyNhanVienKho;
import QuanLyKhoBai.QuanLyNhapKho;
import QuanLyPhanLoai.LoaiThuoc;
import QuanLyPhanLoai.QuanLyLoaiThuoc;
import QuanLySanPham.DanhSachThuoc;
import QuanLySanPham.SanPhamYTe;
import QuanLySanPham.ThuocKeDon;
import QuanLySanPham.ThuocKhongKeDon;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class GiaoDienConsole {
    private static final String LINE = "--------------------------------------------------------------------------";
    private static final DateTimeFormatter USER_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private final Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);
    private final QuanLyLoaiThuoc quanLyLoaiThuoc;
    private final DanhSachThuoc danhSachThuoc;
    private final QuanLyNhanVienKho quanLyNhanVien;
    private final QuanLyKhachHang quanLyKhachHang;
    private final QuanLyNhaCungCap quanLyNhaCungCap;
    private final QuanLyBanHang quanLyBanHang;
    private final QuanLyNhapKho quanLyNhapKho;
    private final QuanLyDonThuoc quanLyDonThuoc;
    private final LuuTruDuLieu luuTruDuLieu;

    public GiaoDienConsole(
            QuanLyLoaiThuoc quanLyLoaiThuoc,
            DanhSachThuoc danhSachThuoc,
            QuanLyNhanVienKho quanLyNhanVien,
            QuanLyKhachHang quanLyKhachHang,
            QuanLyNhaCungCap quanLyNhaCungCap,
            QuanLyBanHang quanLyBanHang,
            QuanLyNhapKho quanLyNhapKho,
            QuanLyDonThuoc quanLyDonThuoc,
            LuuTruDuLieu luuTruDuLieu
    ) {
        this.quanLyLoaiThuoc = quanLyLoaiThuoc;
        this.danhSachThuoc = danhSachThuoc;
        this.quanLyNhanVien = quanLyNhanVien;
        this.quanLyKhachHang = quanLyKhachHang;
        this.quanLyNhaCungCap = quanLyNhaCungCap;
        this.quanLyBanHang = quanLyBanHang;
        this.quanLyNhapKho = quanLyNhapKho;
        this.quanLyDonThuoc = quanLyDonThuoc;
        this.luuTruDuLieu = luuTruDuLieu;
    }

    public void run() throws IOException {
        while (true) {
            printChoiceTable("QUẢN LÝ CỬA HÀNG THUỐC", "Chọn một chức năng để tiếp tục",
                    "1. Thuốc và tồn kho",
                    "2. Loại thuốc",
                    "3. Bán hàng",
                    "4. Nhập kho",
                    "5. Khách hàng, nhân viên, nhà cung cấp",
                    "6. Toa thuốc",
                    "7. Báo cáo doanh thu",
                    "0. Thoát");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) {
                luuTruDuLieu.save();
                System.out.println("\n  Đã thoát chương trình. Tạm biệt!");
                return;
            }
            if (choice < 0 || choice > 7) {
                invalidChoice();
                continue;
            }
            switch (choice) {
                case 1:
                    manageMedicines();
                    break;
                case 2:
                    manageCategories();
                    break;
                case 3:
                    sellMedicine();
                    break;
                case 4:
                    importStock();
                    break;
                case 5:
                    managePartners();
                    break;
                case 6:
                    managePrescriptions();
                    break;
                case 7:
                    showRevenueReport();
                    break;
                default:
                    System.out.println("  Không có lựa chọn này. Vui lòng chọn từ 0 đến 7.");
            }
            luuTruDuLieu.save();
        }
    }

    private void manageMedicines() {
        while (true) {
            printMedicines(danhSachThuoc.getAllMedicines(), "DANH SÁCH THUỐC");
            printSubmenu("1. Thêm thuốc", "2. Sửa thuốc", "3. Xóa thuốc",
                    "4. Thuốc sắp hết hạn", "5. Tìm kiếm thuốc", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addMedicine(); break;
                case 2: updateMedicine(); break;
                case 3: deleteMedicine(); break;
                case 4:
                    Integer days = readInt("  Hiển thị thuốc hết hạn trong bao nhiêu ngày tới? ");
                    if (days != null && days >= 0) {
                        printMedicines(danhSachThuoc.filterExpiringSoon(days), "THUỐC SẮP HẾT HẠN");
                    } else if (days != null) {
                        System.out.println("  Số ngày phải lớn hơn hoặc bằng 0.");
                    }
                    break;
                case 5: searchMedicines(); break;
                default: invalidChoice();
            }
        }
    }

    private void searchMedicines() {
        String keyword = readText("  Nhập mã, tên, mã loại hoặc nhà sản xuất cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<SanPhamYTe> matches = danhSachThuoc.getAllMedicines().stream()
                .filter(medicine -> containsIgnoreCase(medicine.getMedicineId(), query)
                        || containsIgnoreCase(medicine.getName(), query)
                        || containsIgnoreCase(medicine.getCategoryId(), query)
                        || containsIgnoreCase(medicine.getManufacturer(), query))
                .collect(Collectors.toList());
        printMedicines(matches, "KẾT QUẢ TÌM THUỐC");
    }

    private void printMedicines(List<SanPhamYTe> medicines, String title) {
        System.out.println("\n  " + title);
        System.out.printf("  %-8s %-22s %-10s %14s %8s %-12s %s%n",
                "Mã", "Tên thuốc", "Mã loại", "Giá bán", "Tồn", "Hạn dùng", "Cần toa");
        System.out.println("  " + LINE);
        if (medicines.isEmpty()) {
            System.out.println("  Chưa có dữ liệu.");
        }
        for (SanPhamYTe medicine : medicines) {
            System.out.printf("  %-8s %-22s %-10s %14s %8d %-12s %s%n",
                    medicine.getMedicineId(), medicine.getName(), medicine.getCategoryId(),
                    money(medicine.tinhGiaBan()), medicine.getQuantityInStock(),
                    formatDate(medicine.getExpiryDate()), medicine.isPrescriptionRequired() ? "Có" : "Không");
        }
    }

    private void addMedicine() {
        String id = readUniqueId("  Mã thuốc: ",
                candidate -> danhSachThuoc.getMedicine(candidate) != null,
                "Mã thuốc đã tồn tại.");
        String name = readText("  Tên thuốc: ");
        String categoryId = readExistingId("  Mã loại thuốc: ",
                candidate -> quanLyLoaiThuoc.getCategory(candidate) != null,
                "Không tìm thấy loại thuốc.");
        String manufacturer = readText("  Nhà sản xuất: ");
        Double price = readDoubleAtLeast("  Giá bán: ", 0);
        Integer quantity = readIntAtLeast("  Số lượng tồn: ", 0);
        LocalDate expiry = readDate("  Hạn dùng (dd-MM-yyyy, ví dụ 10-10-2030): ");
        Boolean requiresPrescription = readYesNo("  Thuốc cần toa? (c/k): ");
        if (id == null || name == null || categoryId == null || manufacturer == null
                || price == null || quantity == null || expiry == null || requiresPrescription == null) {
            return;
        }
        boolean added = danhSachThuoc.addMedicine(createMedicine(
                id, name, categoryId, manufacturer, price, quantity, expiry, requiresPrescription));
        if (added) saveAllData();
        System.out.println(added ? "  Đã thêm thuốc." : "  Không thể thêm thuốc. Hãy kiểm tra mã thuốc.");
    }

    private void updateMedicine() {
        String id = readExistingId("  Nhập mã thuốc cần sửa: ",
                candidate -> danhSachThuoc.getMedicine(candidate) != null, "Không tìm thấy thuốc.");
        if (id == null) return;
        SanPhamYTe current = danhSachThuoc.getMedicine(id);
        String name = readOptionalText("  Tên thuốc mới", current.getName());
        String categoryId = readOptionalExistingId("  Mã loại mới", current.getCategoryId(),
                candidate -> quanLyLoaiThuoc.getCategory(candidate) != null,
                "Không tìm thấy loại thuốc.");
        String manufacturer = readOptionalText("  Nhà sản xuất mới", current.getManufacturer());
        Double price = readOptionalDoubleAtLeast("  Giá bán mới", current.getUnitPrice(), 0);
        Integer quantity = readOptionalIntAtLeast("  Số lượng tồn mới", current.getQuantityInStock(), 0);
        LocalDate expiry = readOptionalDate("  Hạn dùng mới (dd-MM-yyyy)", current.getExpiryDate());
        Boolean requiresPrescription =
                readOptionalYesNo("  Cần toa mới? (c/k)", current.isPrescriptionRequired());
        if (name == null || categoryId == null || manufacturer == null || price == null
                || quantity == null || requiresPrescription == null) return;
        SanPhamYTe updated = createMedicine(id, name, categoryId, manufacturer,
                price, quantity, expiry, requiresPrescription);
        boolean saved = danhSachThuoc.updateMedicine(updated);
        if (saved) saveAllData();
        System.out.println(saved ? "  Đã cập nhật thuốc." : "  Cập nhật thất bại.");
    }

    private void deleteMedicine() {
        String id = readExistingId("  Nhập mã thuốc cần xóa: ",
                candidate -> danhSachThuoc.getMedicine(candidate) != null, "Không tìm thấy thuốc.");
        if (id == null) return;
        boolean usedInInvoice = quanLyBanHang.getAllInvoices().stream()
                .anyMatch(invoice -> quanLyBanHang.getDetails(invoice.getInvoiceId()).stream()
                        .anyMatch(detail -> id.equals(detail.getMedicineId())));
        boolean usedInPrescription = quanLyDonThuoc.getAllPrescriptions().stream()
                .anyMatch(prescription -> prescription.getMedicineIds().contains(id));
        boolean usedInImport = quanLyNhapKho.getAllImportNotes().stream()
                .anyMatch(note -> note.getMedicineIds().contains(id));
        if (usedInInvoice || usedInPrescription || usedInImport) {
            System.out.println("  Không thể xóa thuốc đã được ghi trong chứng từ hoặc toa thuốc.");
            return;
        }
        boolean deleted = danhSachThuoc.deleteMedicine(id);
        if (deleted) saveAllData();
        System.out.println(deleted ? "  Đã xóa thuốc." : "  Không tìm thấy thuốc.");
    }

    private void manageCategories() {
        while (true) {
            printCategories();
            printSubmenu("1. Thêm loại thuốc", "2. Sửa loại thuốc", "3. Xóa loại thuốc",
                    "4. Tìm kiếm loại thuốc", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addCategory(); break;
                case 2: updateCategory(); break;
                case 3: deleteCategory(); break;
                case 4: searchCategories(); break;
                default: invalidChoice();
            }
        }
    }

    private void printCategories() {
        System.out.println("\n  DANH SÁCH LOẠI THUỐC");
        System.out.printf("  %-10s %-24s %s%n", "Mã loại", "Tên loại", "Mô tả");
        System.out.println("  " + LINE);
        if (quanLyLoaiThuoc.getAllCategories().isEmpty()) System.out.println("  Chưa có dữ liệu.");
        for (LoaiThuoc category : quanLyLoaiThuoc.getAllCategories()) {
            System.out.printf("  %-10s %-24s %s%n",
                    category.getCategoryId(), category.getName(), category.getDescription());
        }
    }

    private void searchCategories() {
        String keyword = readText("  Nhập mã loại hoặc tên loại cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<LoaiThuoc> matches = quanLyLoaiThuoc.getAllCategories().stream()
                .filter(category -> containsIgnoreCase(category.getCategoryId(), query)
                        || containsIgnoreCase(category.getName(), query)
                        || containsIgnoreCase(category.getDescription(), query))
                .collect(Collectors.toList());
        System.out.println("\n  KẾT QUẢ TÌM LOẠI THUỐC");
        if (matches.isEmpty()) System.out.println("  Không tìm thấy loại thuốc.");
        for (LoaiThuoc category : matches) {
            System.out.printf("  %-10s %-24s %s%n",
                    category.getCategoryId(), category.getName(), category.getDescription());
        }
    }

    private void addCategory() {
        String id = readUniqueId("  Mã loại: ",
                candidate -> quanLyLoaiThuoc.getCategory(candidate) != null,
                "Mã loại thuốc đã tồn tại.");
        String name = readText("  Tên loại: ");
        String description = readText("  Mô tả: ");
        if (id == null || name == null || description == null) {
            return;
        }
        boolean added = quanLyLoaiThuoc.addCategory(new LoaiThuoc(id, name, description));
        if (added) saveAllData();
        System.out.println(added ? "  Đã thêm loại thuốc." : "  Không thể thêm loại thuốc.");
    }

    private void updateCategory() {
        String id = readExistingId("  Mã loại cần sửa: ",
                candidate -> quanLyLoaiThuoc.getCategory(candidate) != null, "Không tìm thấy loại thuốc.");
        if (id == null) return;
        LoaiThuoc current = quanLyLoaiThuoc.getCategory(id);
        String name = readOptionalText("  Tên loại mới", current.getName());
        String description = readOptionalText("  Mô tả mới", current.getDescription());
        if (id == null || name == null || description == null) return;
        boolean updated = quanLyLoaiThuoc.updateCategory(id, name, description);
        if (updated) saveAllData();
        System.out.println(updated ? "  Đã cập nhật loại thuốc." : "  Không tìm thấy loại thuốc.");
    }

    private void deleteCategory() {
        String id = readExistingId("  Mã loại cần xóa: ",
                candidate -> quanLyLoaiThuoc.getCategory(candidate) != null, "Không tìm thấy loại thuốc.");
        if (id == null) return;
        for (SanPhamYTe medicine : danhSachThuoc.getAllMedicines()) {
            if (id.equals(medicine.getCategoryId())) {
                System.out.println("  Không thể xóa loại đang được thuốc sử dụng.");
                return;
            }
        }
        boolean deleted = quanLyLoaiThuoc.deleteCategory(id);
        if (deleted) saveAllData();
        System.out.println(deleted ? "  Đã xóa loại thuốc." : "  Không tìm thấy loại thuốc.");
    }

    private void sellMedicine() {
        while (true) {
            printInvoices();
            printSubmenu("1. Tạo hóa đơn bán thuốc", "2. Tìm kiếm hóa đơn", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            if (choice == 1) createSale();
            else if (choice == 2) searchInvoices();
            else invalidChoice();
        }
    }

    private void searchInvoices() {
        String keyword = readText("  Nhập mã hóa đơn, mã khách hoặc mã nhân viên cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<PhieuXuat> matches = quanLyBanHang.getAllInvoices().stream()
                .filter(invoice -> containsIgnoreCase(invoice.getInvoiceId(), query)
                        || containsIgnoreCase(invoice.getCustomerId(), query)
                        || containsIgnoreCase(invoice.getEmployeeId(), query))
                .collect(Collectors.toList());
        printInvoices(matches, "KẾT QUẢ TÌM HÓA ĐƠN");
    }

    private void printInvoices() {
        printInvoices(quanLyBanHang.getAllInvoices(), "DANH SÁCH HÓA ĐƠN");
    }

    private void printInvoices(List<PhieuXuat> invoices, String title) {
        System.out.println("\n  " + title);
        System.out.printf("  %-18s %-10s %-10s %-12s %14s %-16s%n",
                "Mã hóa đơn", "Khách hàng", "Nhân viên", "Ngày tạo", "Tổng tiền", "Trạng thái");
        System.out.println("  " + LINE);
        if (invoices.isEmpty()) System.out.println("  Chưa có hóa đơn.");
        for (PhieuXuat invoice : invoices) {
            System.out.printf("  %-18s %-10s %-10s %-12s %14s %-16s%n",
                    invoice.getInvoiceId(), invoice.getCustomerId(), invoice.getEmployeeId(),
                    invoice.getCreatedAt().toLocalDate(), money(invoice.getTotalAmount()),
                    invoice.isPaid() ? "Đã thanh toán" : "Chưa thanh toán");
        }
    }

    private void createSale() {
        String medicineId = readExistingId("  Mã thuốc: ",
                candidate -> danhSachThuoc.getMedicine(candidate) != null, "Không tìm thấy thuốc.");
        String customerId = readExistingId("  Mã khách hàng: ",
                candidate -> quanLyKhachHang.getCustomer(candidate) != null, "Không tìm thấy khách hàng.");
        String employeeId = readExistingId("  Mã nhân viên: ",
                candidate -> quanLyNhanVien.getEmployee(candidate) != null, "Không tìm thấy nhân viên.");
        if (medicineId == null || customerId == null || employeeId == null) return;
        SanPhamYTe medicine = danhSachThuoc.getMedicine(medicineId);
        Integer quantity;
        do {
            quantity = readIntAtLeast(
                    "  Số lượng (còn " + medicine.getQuantityInStock() + "): ", 1);
            if (quantity != null && quantity > medicine.getQuantityInStock()) {
                System.out.println("  Số lượng vượt quá tồn kho. Vui lòng nhập lại.");
            } else {
                break;
            }
        } while (input.hasNextLine());
        String payment = readText("  Phương thức thanh toán: ");
        if (quantity == null || payment == null) {
            return;
        }
        if (medicine.isPrescriptionRequired()) {
            String prescriptionId;
            do {
                prescriptionId = readExistingId("  Mã toa thuốc: ",
                        candidate -> quanLyDonThuoc.getPrescription(candidate) != null,
                        "Không tìm thấy toa thuốc.");
                if (prescriptionId == null
                        || quanLyDonThuoc.isMedicineIncluded(prescriptionId, medicineId)) break;
                System.out.println("  Toa thuốc không có thuốc này. Vui lòng nhập mã toa khác.");
            } while (input.hasNextLine());
            if (prescriptionId == null) {
                return;
            }
        }
        String invoiceId = "HD" + System.currentTimeMillis();
        PhieuXuat invoice = quanLyBanHang.createInvoice(invoiceId, customerId, employeeId, payment);
        if (!quanLyBanHang.addMedicineToInvoice(invoiceId, medicineId, quantity)) {
            System.out.println("  Bán thuốc thất bại. Kiểm tra số lượng tồn kho.");
            return;
        }
        quanLyBanHang.finalizeInvoice(invoiceId);
        saveAllData();
        System.out.println("  Tạo hóa đơn thành công: " + invoiceId);
        System.out.println("  Tổng thanh toán: " + money(invoice.getTotalAmount()));
    }

    private void importStock() {
        while (true) {
            printImportNotes();
            printSubmenu("1. Tạo phiếu nhập", "2. Nhập thêm thuốc",
                    "3. Tìm kiếm phiếu nhập", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            if (choice == 1) createImportNote();
            else if (choice == 2) importMedicineStock();
            else if (choice == 3) searchImportNotes();
            else invalidChoice();
        }
    }

    private void printImportNotes() {
        printImportNotes(quanLyNhapKho.getAllImportNotes(), "PHIẾU NHẬP KHO");
    }

    private void printImportNotes(List<PhieuNhap> notes, String title) {
        System.out.println("\n  " + title);
        System.out.printf("  %-12s %-12s %-12s %-12s %14s %s%n",
                "Mã phiếu", "Nhà cung cấp", "Nhân viên", "Ngày nhập", "Tổng tiền", "Mã thuốc");
        System.out.println("  " + LINE);
        if (notes.isEmpty()) System.out.println("  Chưa có phiếu nhập.");
        for (PhieuNhap note : notes) {
            System.out.printf("  %-12s %-12s %-12s %-12s %14s %s%n",
                    note.getImportNoteId(), note.getSupplierId(), note.getEmployeeId(),
                    formatDate(note.getImportDate()), money(note.getTotalAmount()), String.join(", ", note.getMedicineIds()));
        }
    }

    private void searchImportNotes() {
        String keyword = readText("  Nhập mã phiếu, nhà cung cấp, nhân viên hoặc mã thuốc cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<PhieuNhap> matches = quanLyNhapKho.getAllImportNotes().stream()
                .filter(note -> containsIgnoreCase(note.getImportNoteId(), query)
                        || containsIgnoreCase(note.getSupplierId(), query)
                        || containsIgnoreCase(note.getEmployeeId(), query)
                        || note.getMedicineIds().stream().anyMatch(id -> containsIgnoreCase(id, query)))
                .collect(Collectors.toList());
        printImportNotes(matches, "KẾT QUẢ TÌM PHIẾU NHẬP");
    }

    private void importMedicineStock() {
        String noteId = readExistingId("  Mã phiếu nhập: ",
                candidate -> quanLyNhapKho.getImportNote(candidate) != null, "Không tìm thấy phiếu nhập.");
        String medicineId = readExistingId("  Mã thuốc: ",
                candidate -> danhSachThuoc.getMedicine(candidate) != null, "Không tìm thấy thuốc.");
        Integer quantity = readIntAtLeast("  Số lượng nhập: ", 1);
        Double price = readDoubleAtLeast("  Giá nhập mỗi đơn vị: ", 0);
        if (noteId == null || medicineId == null || quantity == null || price == null) return;
        boolean imported = quanLyNhapKho.importMedicine(noteId, medicineId, quantity, price);
        if (imported) saveAllData();
        System.out.println(imported
                ? "  Nhập kho thành công."
                : "  Nhập kho thất bại. Kiểm tra mã phiếu và mã thuốc.");
    }

    private void createImportNote() {
        String id = readUniqueId("  Mã phiếu nhập: ",
                candidate -> quanLyNhapKho.getImportNote(candidate) != null,
                "Mã phiếu nhập đã tồn tại.");
        String supplierId = readExistingId("  Mã nhà cung cấp: ",
                candidate -> quanLyNhaCungCap.getSupplier(candidate) != null, "Không tìm thấy nhà cung cấp.");
        String employeeId = readExistingId("  Mã nhân viên: ",
                candidate -> quanLyNhanVien.getEmployee(candidate) != null, "Không tìm thấy nhân viên.");
        if (id == null || supplierId == null || employeeId == null) return;
        boolean created = quanLyNhapKho.addImportNote(new PhieuNhap(id, supplierId, employeeId, 0));
        if (created) saveAllData();
        System.out.println(created ? "  Đã tạo phiếu nhập." : "  Không thể tạo phiếu nhập.");
    }

    private void managePartners() {
        while (true) {
            System.out.println("\n  QUẢN LÝ ĐỐI TÁC");
            printSubmenu("1. Khách hàng", "2. Nhân viên", "3. Nhà cung cấp", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: manageCustomers(); break;
                case 2: manageEmployees(); break;
                case 3: manageSuppliers(); break;
                default: invalidChoice();
            }
        }
    }

    private void manageCustomers() {
        while (true) {
            printCustomers();
            printSubmenu("1. Thêm khách hàng", "2. Sửa khách hàng", "3. Xóa khách hàng",
                    "4. Tìm kiếm khách hàng", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addCustomer(); break;
                case 2: updateCustomer(); break;
                case 3: deleteCustomer(); break;
                case 4: searchCustomers(); break;
                default: invalidChoice();
            }
        }
    }

    private void printCustomers() {
        System.out.println("\n  DANH SÁCH KHÁCH HÀNG");
        System.out.printf("  %-10s %-24s %-16s %8s %12s%n", "Mã khách", "Họ tên", "Điện thoại", "Điểm", "Hạng");
        System.out.println("  " + LINE);
        if (quanLyKhachHang.getAllCustomers().isEmpty()) System.out.println("  Chưa có dữ liệu.");
        for (KhachHang customer : quanLyKhachHang.getAllCustomers()) {
            System.out.printf("  %-10s %-24s %-16s %8d %12s%n",
                    customer.getCustomerId(), customer.getName(), customer.getPhone(),
                    customer.getLoyaltyPoints(), customer.getMemberTier());
        }
    }

    private void searchCustomers() {
        String keyword = readText("  Nhập mã, tên hoặc số điện thoại cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<KhachHang> matches = quanLyKhachHang.getAllCustomers().stream()
                .filter(customer -> containsIgnoreCase(customer.getCustomerId(), query)
                        || containsIgnoreCase(customer.getName(), query)
                        || containsIgnoreCase(customer.getPhone(), query))
                .collect(Collectors.toList());
        System.out.println("\n  KẾT QUẢ TÌM KHÁCH HÀNG");
        if (matches.isEmpty()) System.out.println("  Không tìm thấy khách hàng.");
        if (matches.size() > 1) System.out.println("  Có nhiều kết quả; xem mã khách hàng để phân biệt.");
        for (KhachHang customer : matches) {
            System.out.printf("  %-10s %-24s %-16s %8d %12s%n",
                    customer.getCustomerId(), customer.getName(), customer.getPhone(),
                    customer.getLoyaltyPoints(), customer.getMemberTier());
        }
    }

    private void addCustomer() {
        String id = readUniqueId("  Mã khách hàng: ",
                candidate -> quanLyKhachHang.getCustomer(candidate) != null,
                "Mã khách hàng đã tồn tại.");
        String name = readText("  Họ tên: ");
        String phone = readPhoneNumber("  Số điện thoại: ");
        if (id != null && name != null && phone != null) {
            boolean added = quanLyKhachHang.addCustomer(new KhachHang(id, name, phone, 0));
            if (added) saveAllData();
            System.out.println(added ? "  Đã thêm khách hàng." : "  Không thể thêm khách hàng.");
        }
    }

    private void updateCustomer() {
        String id = readExistingId("  Mã khách hàng cần sửa: ",
                candidate -> quanLyKhachHang.getCustomer(candidate) != null, "Không tìm thấy khách hàng.");
        if (id == null) return;
        KhachHang customer = quanLyKhachHang.getCustomer(id);
        String name = readOptionalText("  Họ tên mới", customer.getName());
        String phone = readOptionalPhoneNumber("  Số điện thoại mới", customer.getPhone());
        if (name == null || phone == null) return;
        customer.setName(name);
        customer.setPhone(phone);
        boolean updated = quanLyKhachHang.updateCustomer(customer);
        if (updated) saveAllData();
        System.out.println(updated ? "  Đã cập nhật khách hàng." : "  Cập nhật thất bại.");
    }

    private void deleteCustomer() {
        String id = readExistingId("  Mã khách hàng cần xóa: ",
                candidate -> quanLyKhachHang.getCustomer(candidate) != null, "Không tìm thấy khách hàng.");
        if (id == null) return;
        for (PhieuXuat invoice : quanLyBanHang.getAllInvoices()) {
            if (id.equals(invoice.getCustomerId())) {
                System.out.println("  Không thể xóa khách hàng đã có hóa đơn.");
                return;
            }
        }
        if (quanLyDonThuoc.getAllPrescriptions().stream()
                .anyMatch(prescription -> id.equals(prescription.getCustomerId()))) {
            System.out.println("  Không thể xóa khách hàng đã có toa thuốc.");
            return;
        }
        boolean deleted = quanLyKhachHang.deleteCustomer(id);
        if (deleted) saveAllData();
        System.out.println(deleted ? "  Đã xóa khách hàng." : "  Không tìm thấy khách hàng.");
    }

    private void manageEmployees() {
        while (true) {
            printEmployees();
            printSubmenu("1. Thêm nhân viên", "2. Sửa nhân viên", "3. Xóa nhân viên",
                    "4. Tìm kiếm nhân viên", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addEmployee(); break;
                case 2: updateEmployee(); break;
                case 3: deleteEmployee(); break;
                case 4: searchEmployees(); break;
                default: invalidChoice();
            }
        }
    }

    private void printEmployees() {
        System.out.println("\n  DANH SÁCH NHÂN VIÊN");
        System.out.printf("  %-10s %-24s %-16s %-16s %14s%n",
                "Mã NV", "Họ tên", "Chức vụ", "Điện thoại", "Lương tháng");
        System.out.println("  " + LINE);
        if (quanLyNhanVien.getAllEmployees().isEmpty()) System.out.println("  Chưa có dữ liệu.");
        for (NhanVienKho employee : quanLyNhanVien.getAllEmployees()) {
            System.out.printf("  %-10s %-24s %-16s %-16s %14s%n",
                    employee.getEmployeeId(), employee.getName(), employee.getRole(),
                    employee.getPhone(), money(employee.calculateMonthlySalary()));
        }
    }

    private void searchEmployees() {
        String keyword = readText("  Nhập mã, tên, chức vụ hoặc số điện thoại cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<NhanVienKho> matches = quanLyNhanVien.getAllEmployees().stream()
                .filter(employee -> containsIgnoreCase(employee.getEmployeeId(), query)
                        || containsIgnoreCase(employee.getName(), query)
                        || containsIgnoreCase(employee.getRole(), query)
                        || containsIgnoreCase(employee.getPhone(), query))
                .collect(Collectors.toList());
        System.out.println("\n  KẾT QUẢ TÌM NHÂN VIÊN");
        if (matches.isEmpty()) System.out.println("  Không tìm thấy nhân viên.");
        if (matches.size() > 1) System.out.println("  Có nhiều kết quả; xem mã nhân viên để phân biệt.");
        for (NhanVienKho employee : matches) {
            System.out.printf("  %-10s %-24s %-16s %-16s %14s%n",
                    employee.getEmployeeId(), employee.getName(), employee.getRole(),
                    employee.getPhone(), money(employee.calculateMonthlySalary()));
        }
    }

    private void addEmployee() {
        String id = readUniqueId("  Mã nhân viên: ",
                candidate -> quanLyNhanVien.getEmployee(candidate) != null,
                "Mã nhân viên đã tồn tại.");
        String name = readText("  Họ tên: ");
        String role = readText("  Chức vụ: ");
        String phone = readPhoneNumber("  Số điện thoại: ");
        Double salary = readDoubleAtLeast("  Lương cơ bản: ", 0);
        if (id != null && name != null && role != null && phone != null && salary != null) {
            boolean added = quanLyNhanVien.addEmployee(new NhanVienKho(id, name, role, phone, salary, 0));
            if (added) saveAllData();
            System.out.println(added ? "  Đã thêm nhân viên." : "  Không thể thêm nhân viên.");
        }
    }

    private void updateEmployee() {
        String id = readExistingId("  Mã nhân viên cần sửa: ",
                candidate -> quanLyNhanVien.getEmployee(candidate) != null, "Không tìm thấy nhân viên.");
        if (id == null) return;
        NhanVienKho current = quanLyNhanVien.getEmployee(id);
        String name = readOptionalText("  Họ tên mới", current.getName());
        String role = readOptionalText("  Chức vụ mới", current.getRole());
        String phone = readOptionalPhoneNumber("  Số điện thoại mới", current.getPhone());
        Double salary = readOptionalDoubleAtLeast("  Lương cơ bản mới", current.getBaseSalary(), 0);
        Double commission = readOptionalDoubleAtLeast("  Hoa hồng mới", current.getCommission(), 0);
        if (name == null || role == null || phone == null || salary == null || commission == null) return;
        NhanVienKho updated = new NhanVienKho(id, name, role, phone, salary, commission);
        boolean saved = quanLyNhanVien.updateEmployee(updated);
        if (saved) saveAllData();
        System.out.println(saved ? "  Đã cập nhật nhân viên." : "  Cập nhật thất bại.");
    }

    private void deleteEmployee() {
        String id = readExistingId("  Mã nhân viên cần xóa: ",
                candidate -> quanLyNhanVien.getEmployee(candidate) != null, "Không tìm thấy nhân viên.");
        if (id == null) return;
        for (PhieuXuat invoice : quanLyBanHang.getAllInvoices()) {
            if (id.equals(invoice.getEmployeeId())) {
                System.out.println("  Không thể xóa nhân viên đã lập hóa đơn.");
                return;
            }
        }
        if (quanLyNhapKho.getAllImportNotes().stream().anyMatch(note -> id.equals(note.getEmployeeId()))) {
            System.out.println("  Không thể xóa nhân viên đã lập phiếu nhập.");
            return;
        }
        boolean deleted = quanLyNhanVien.deleteEmployee(id);
        if (deleted) saveAllData();
        System.out.println(deleted ? "  Đã xóa nhân viên." : "  Không tìm thấy nhân viên.");
    }

    private void manageSuppliers() {
        while (true) {
            printSuppliers();
            printSubmenu("1. Thêm nhà cung cấp", "2. Sửa nhà cung cấp", "3. Xóa nhà cung cấp",
                    "4. Tìm kiếm nhà cung cấp", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addSupplier(); break;
                case 2: updateSupplier(); break;
                case 3: deleteSupplier(); break;
                case 4: searchSuppliers(); break;
                default: invalidChoice();
            }
        }
    }

    private void printSuppliers() {
        System.out.println("\n  DANH SÁCH NHÀ CUNG CẤP");
        System.out.printf("  %-12s %-26s %-16s %s%n", "Mã NCC", "Tên nhà cung cấp", "Điện thoại", "Địa chỉ");
        System.out.println("  " + LINE);
        if (quanLyNhaCungCap.getAllSuppliers().isEmpty()) System.out.println("  Chưa có dữ liệu.");
        for (NhaCungCap supplier : quanLyNhaCungCap.getAllSuppliers()) {
            System.out.printf("  %-12s %-26s %-16s %s%n",
                    supplier.getSupplierId(), supplier.getName(), supplier.getPhone(), supplier.getAddress());
        }
    }

    private void searchSuppliers() {
        String keyword = readText("  Nhập mã, tên, số điện thoại hoặc địa chỉ cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<NhaCungCap> matches = quanLyNhaCungCap.getAllSuppliers().stream()
                .filter(supplier -> containsIgnoreCase(supplier.getSupplierId(), query)
                        || containsIgnoreCase(supplier.getName(), query)
                        || containsIgnoreCase(supplier.getPhone(), query)
                        || containsIgnoreCase(supplier.getAddress(), query))
                .collect(Collectors.toList());
        System.out.println("\n  KẾT QUẢ TÌM NHÀ CUNG CẤP");
        if (matches.isEmpty()) System.out.println("  Không tìm thấy nhà cung cấp.");
        if (matches.size() > 1) System.out.println("  Có nhiều kết quả; xem mã nhà cung cấp để phân biệt.");
        for (NhaCungCap supplier : matches) {
            System.out.printf("  %-12s %-26s %-16s %s%n",
                    supplier.getSupplierId(), supplier.getName(), supplier.getPhone(), supplier.getAddress());
        }
    }

    private void addSupplier() {
        String id = readUniqueId("  Mã nhà cung cấp: ",
                candidate -> quanLyNhaCungCap.getSupplier(candidate) != null,
                "Mã nhà cung cấp đã tồn tại.");
        String name = readText("  Tên nhà cung cấp: ");
        String phone = readPhoneNumber("  Số điện thoại: ");
        String address = readText("  Địa chỉ: ");
        if (id != null && name != null && phone != null && address != null) {
            boolean added = quanLyNhaCungCap.addSupplier(new NhaCungCap(id, name, phone, address));
            if (added) saveAllData();
            System.out.println(added ? "  Đã thêm nhà cung cấp." : "  Không thể thêm nhà cung cấp.");
        }
    }

    private void updateSupplier() {
        String id = readExistingId("  Mã nhà cung cấp cần sửa: ",
                candidate -> quanLyNhaCungCap.getSupplier(candidate) != null, "Không tìm thấy nhà cung cấp.");
        if (id == null) return;
        NhaCungCap current = quanLyNhaCungCap.getSupplier(id);
        String name = readOptionalText("  Tên mới", current.getName());
        String phone = readOptionalPhoneNumber("  Số điện thoại mới", current.getPhone());
        String address = readOptionalText("  Địa chỉ mới", current.getAddress());
        if (name == null || phone == null || address == null) return;
        boolean updated = quanLyNhaCungCap.updateSupplier(new NhaCungCap(id, name, phone, address));
        if (updated) saveAllData();
        System.out.println(updated ? "  Đã cập nhật nhà cung cấp." : "  Cập nhật thất bại.");
    }

    private void deleteSupplier() {
        String id = readExistingId("  Mã nhà cung cấp cần xóa: ",
                candidate -> quanLyNhaCungCap.getSupplier(candidate) != null, "Không tìm thấy nhà cung cấp.");
        if (id == null) return;
        boolean usedByImport = quanLyNhapKho.getAllImportNotes().stream()
                .anyMatch(note -> id.equals(note.getSupplierId()));
        if (usedByImport) {
            System.out.println("  Không thể xóa nhà cung cấp đã có phiếu nhập.");
            return;
        }
        boolean deleted = quanLyNhaCungCap.deleteSupplier(id);
        if (deleted) saveAllData();
        System.out.println(deleted ? "  Đã xóa nhà cung cấp." : "  Không tìm thấy nhà cung cấp.");
    }

    private void managePrescriptions() {
        while (true) {
            printPrescriptions();
            printSubmenu("1. Tạo toa thuốc", "2. Kiểm tra thuốc trong toa",
                    "3. Tìm kiếm toa thuốc", "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            if (choice == 1) {
                addPrescription();
            } else if (choice == 2) {
                String prescriptionId = readText("  Mã toa: ");
                String medicineId = readText("  Mã thuốc: ");
                if (prescriptionId != null && medicineId != null) {
                    System.out.println(quanLyDonThuoc.isMedicineIncluded(prescriptionId, medicineId)
                            ? "  Thuốc có trong toa."
                            : "  Không tìm thấy thuốc trong toa.");
                }
            } else if (choice == 3) {
                searchPrescriptions();
            } else {
                invalidChoice();
            }
        }
    }

    private void printPrescriptions() {
        printPrescriptions(quanLyDonThuoc.getAllPrescriptions(), "DANH SÁCH TOA THUỐC");
    }

    private void printPrescriptions(List<DonThuoc> prescriptions, String title) {
        System.out.println("\n  " + title);
        System.out.printf("  %-12s %-22s %-12s %-20s %-12s %s%n",
                "Mã toa", "Bệnh nhân", "Mã khách", "Bác sĩ", "Ngày kê", "Mã thuốc");
        System.out.println("  " + LINE);
        if (prescriptions.isEmpty()) System.out.println("  Chưa có toa thuốc.");
        for (DonThuoc prescription : prescriptions) {
            System.out.printf("  %-12s %-22s %-12s %-20s %-12s %s%n",
                    prescription.getPrescriptionId(), prescription.getPatientName(),
                    prescription.getCustomerId(), prescription.getDoctorName(),
                    formatDate(prescription.getIssueDate()), String.join(", ", prescription.getMedicineIds()));
        }
    }

    private void searchPrescriptions() {
        String keyword = readText("  Nhập mã toa, tên bệnh nhân, mã khách, bác sĩ hoặc mã thuốc cần tìm: ");
        if (keyword == null) return;
        String query = keyword.toLowerCase(Locale.ROOT);
        List<DonThuoc> matches = quanLyDonThuoc.getAllPrescriptions().stream()
                .filter(prescription -> containsIgnoreCase(prescription.getPrescriptionId(), query)
                        || containsIgnoreCase(prescription.getPatientName(), query)
                        || containsIgnoreCase(prescription.getCustomerId(), query)
                        || containsIgnoreCase(prescription.getDoctorName(), query)
                        || prescription.getMedicineIds().stream().anyMatch(id -> containsIgnoreCase(id, query)))
                .collect(Collectors.toList());
        printPrescriptions(matches, "KẾT QUẢ TÌM TOA THUỐC");
    }

    private void addPrescription() {
        String id = readUniqueId("  Mã toa: ",
                candidate -> quanLyDonThuoc.getPrescription(candidate) != null,
                "Mã toa thuốc đã tồn tại.");
        String customerId = readExistingId("  Mã khách hàng: ",
                candidate -> quanLyKhachHang.getCustomer(candidate) != null, "Không tìm thấy khách hàng.");
        String patient = readText("  Tên bệnh nhân: ");
        String doctor = readText("  Tên bác sĩ: ");
        String medicineId = readExistingId("  Mã thuốc: ",
                candidate -> danhSachThuoc.getMedicine(candidate) != null, "Không tìm thấy thuốc.");
        if (id == null || customerId == null || patient == null || doctor == null || medicineId == null) {
            return;
        }
        DonThuoc prescription = new DonThuoc(id, customerId, patient, doctor, LocalDate.now());
        prescription.addMedicine(medicineId);
        boolean added = quanLyDonThuoc.addPrescription(prescription);
        if (added) saveAllData();
        System.out.println(added ? "  Đã tạo toa thuốc." : "  Không thể tạo toa thuốc.");
    }

    private void showRevenueReport() {
        while (true) {
            System.out.println("\n  BÁO CÁO");
            printSubmenu("1. Doanh thu hôm nay",
                    "2. Doanh thu tháng (nhập năm và tháng)",
                    "3. Thuốc hết hạn đến ngày (nhập ngày-tháng-năm)",
                    "0. Quay lại");
            Integer choice = readChoice("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            InBaoCao report = new InBaoCao(quanLyBanHang.getAllInvoices());
            if (choice == 1) {
                LocalDate today = LocalDate.now();
                System.out.println("  Doanh thu ngày " + today + ": "
                        + money(report.getDailyRevenue(today)));
            } else if (choice == 2) {
                Integer year = readIntInRange("  Nhập năm (ví dụ 2026): ", 1, 9999);
                Integer month = readIntInRange("  Nhập tháng từ 1 đến 12 (ví dụ 11): ", 1, 12);
                if (year != null && month != null) {
                    System.out.println("  Doanh thu tháng " + month + "/" + year + ": "
                            + money(report.getMonthlyRevenue(year, month)));
                }
            } else if (choice == 3) {
                LocalDate deadline = readDate("  Nhập ngày hết hạn cần xem đến (dd-MM-yyyy, ví dụ 31-12-2026): ");
                if (deadline != null) {
                    printMedicines(danhSachThuoc.filterExpiringBy(deadline),
                            "THUỐC HẾT HẠN ĐẾN " + formatDate(deadline));
                }
            } else {
                invalidChoice();
            }
        }
    }

    private void printSubmenu(String... options) {
        printChoiceTable("CHỌN THAO TÁC", "", options);
    }

    private void printChoiceTable(String title, String subtitle, String... options) {
        int labelWidth = "Chức năng".length();
        String[] codes = new String[options.length];
        String[] labels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            int separator = options[i].indexOf(". ");
            if (separator > 0) {
                codes[i] = options[i].substring(0, separator);
                labels[i] = options[i].substring(separator + 2);
            } else {
                codes[i] = "";
                labels[i] = options[i];
            }
            labelWidth = Math.max(labelWidth, labels[i].length());
        }

        String border = "  +" + repeat("-", 6) + "+" + repeat("-", labelWidth + 2) + "+";
        System.out.println();
        System.out.println("  " + title);
        if (!subtitle.isEmpty()) System.out.println("  " + subtitle);
        System.out.println(border);
        System.out.printf("  | %-4s | %-"+labelWidth+"s |%n", "STT", "Chức năng");
        System.out.println(border);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("  | %-4s | %-"+labelWidth+"s |%n", codes[i], labels[i]);
        }
        System.out.println(border);
    }

    private String repeat(String value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; i++) result.append(value);
        return result.toString();
    }

    private void invalidChoice() {
        System.out.println("  Lựa chọn không hợp lệ. Vui lòng thử lại.");
    }

    private String readText(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            String value = input.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("  Giá trị không được để trống. Vui lòng nhập lại.");
        }
        return null;
    }

    private String readPhoneNumber(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            String phone = input.nextLine().trim();
            if (phone.matches("[0-9]{10}")) {
                return phone;
            }
            System.out.println("  Số điện thoại phải gồm đúng 10 chữ số. Vui lòng nhập lại.");
        }
        return null;
    }

    private String readOptionalText(String prompt, String currentValue) {
        System.out.print(prompt + " [" + currentValue + "]: ");
        System.out.flush();
        if (!input.hasNextLine()) return null;
        String value = input.nextLine().trim();
        return value.isEmpty() ? currentValue : value;
    }

    private String readExistingId(String prompt, Predicate<String> exists, String notFoundMessage) {
        while (input.hasNextLine()) {
            String id = readText(prompt);
            if (id == null) return null;
            if (exists.test(id)) return id;
            System.out.println("  " + notFoundMessage + " Vui lòng nhập lại.");
        }
        return null;
    }

    private String readOptionalExistingId(
            String prompt, String currentValue, Predicate<String> exists, String notFoundMessage) {
        while (input.hasNextLine()) {
            String value = readOptionalText(prompt, currentValue);
            if (value == null) return null;
            if (exists.test(value)) return value;
            System.out.println("  " + notFoundMessage + " Vui lòng nhập lại hoặc nhấn Enter để giữ nguyên.");
        }
        return null;
    }

    private String readOptionalPhoneNumber(String prompt, String currentValue) {
        while (input.hasNextLine()) {
            System.out.print(prompt + " [" + currentValue + "]: ");
            System.out.flush();
            String phone = input.nextLine().trim();
            if (phone.isEmpty()) return currentValue;
            if (phone.matches("[0-9]{10}")) return phone;
            System.out.println("  Số điện thoại phải gồm đúng 10 chữ số. Vui lòng nhập lại.");
        }
        return null;
    }

    private Double readOptionalDouble(String prompt, double currentValue) {
        while (input.hasNextLine()) {
            System.out.print(prompt + " [" + currentValue + "]: ");
            System.out.flush();
            String value = input.nextLine().trim();
            if (value.isEmpty()) return currentValue;
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số hợp lệ hoặc nhấn Enter để giữ nguyên.");
            }
        }
        return null;
    }

    private Integer readOptionalInt(String prompt, int currentValue) {
        while (input.hasNextLine()) {
            System.out.print(prompt + " [" + currentValue + "]: ");
            System.out.flush();
            String value = input.nextLine().trim();
            if (value.isEmpty()) return currentValue;
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số nguyên hoặc nhấn Enter để giữ nguyên.");
            }
        }
        return null;
    }

    private LocalDate readOptionalDate(String prompt, LocalDate currentValue) {
        while (input.hasNextLine()) {
            String currentText = currentValue == null ? "không có" : formatDate(currentValue);
            System.out.print(prompt + " [" + currentText + "]: ");
            System.out.flush();
            String value = input.nextLine().trim();
            if (value.isEmpty()) return currentValue;
            try {
                return LocalDate.parse(value, USER_DATE_FORMAT);
            } catch (DateTimeParseException exception) {
                System.out.println("  Ngày không hợp lệ. Dùng dd-MM-yyyy hoặc nhấn Enter để giữ nguyên.");
            }
        }
        return null;
    }

    private Boolean readOptionalYesNo(String prompt, boolean currentValue) {
        while (input.hasNextLine()) {
            System.out.print(prompt + " [" + (currentValue ? "c" : "k") + "]: ");
            System.out.flush();
            String value = input.nextLine().trim();
            if (value.isEmpty()) return currentValue;
            if (value.equalsIgnoreCase("c")) return true;
            if (value.equalsIgnoreCase("k")) return false;
            System.out.println("  Nhập c (có), k (không), hoặc nhấn Enter để giữ nguyên.");
        }
        return null;
    }

    private String readUniqueId(String prompt, Predicate<String> alreadyExists, String duplicateMessage) {
        while (input.hasNextLine()) {
            String id = readText(prompt);
            if (id == null) return null;
            if (!alreadyExists.test(id)) return id;
            System.out.println("  " + duplicateMessage + " Vui lòng nhập mã khác.");
        }
        return null;
    }

    private Integer readInt(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số nguyên.");
            }
        }
        return null;
    }

    private Integer readIntInRange(String prompt, int minimum, int maximum) {
        while (input.hasNextLine()) {
            Integer value = readInt(prompt);
            if (value == null) return null;
            if (value >= minimum && value <= maximum) return value;
            System.out.println("  Giá trị phải từ " + minimum + " đến " + maximum + ". Vui lòng nhập lại.");
        }
        return null;
    }

    private Integer readIntAtLeast(String prompt, int minimum) {
        return readIntInRange(prompt, minimum, Integer.MAX_VALUE);
    }

    private Integer readOptionalIntAtLeast(String prompt, int currentValue, int minimum) {
        while (input.hasNextLine()) {
            Integer value = readOptionalInt(prompt, currentValue);
            if (value == null || value >= minimum) return value;
            System.out.println("  Giá trị phải lớn hơn hoặc bằng " + minimum
                    + ". Vui lòng nhập lại hoặc nhấn Enter để giữ nguyên.");
        }
        return null;
    }

    private Integer readChoice(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            String value = input.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số nguyên.");
            }
        }
        return null;
    }

    private Double readDouble(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            try {
                return Double.parseDouble(input.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số hợp lệ.");
            }
        }
        return null;
    }

    private Double readDoubleAtLeast(String prompt, double minimum) {
        while (input.hasNextLine()) {
            Double value = readDouble(prompt);
            if (value == null) return null;
            if (Double.isFinite(value) && value >= minimum) return value;
            System.out.println("  Giá trị phải là số hữu hạn và không nhỏ hơn " + minimum
                    + ". Vui lòng nhập lại.");
        }
        return null;
    }

    private Double readOptionalDoubleAtLeast(String prompt, double currentValue, double minimum) {
        while (input.hasNextLine()) {
            Double value = readOptionalDouble(prompt, currentValue);
            if (value == null || (Double.isFinite(value) && value >= minimum)) return value;
            System.out.println("  Giá trị phải là số hữu hạn và không nhỏ hơn " + minimum
                    + ". Vui lòng nhập lại hoặc nhấn Enter để giữ nguyên.");
        }
        return null;
    }

    private LocalDate readDate(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            try {
                return LocalDate.parse(input.nextLine().trim(), USER_DATE_FORMAT);
            } catch (DateTimeParseException exception) {
                System.out.println("  Ngày không hợp lệ. Dùng định dạng dd-MM-yyyy, ví dụ 10-10-2030.");
            }
        }
        return null;
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : USER_DATE_FORMAT.format(date);
    }

    private Boolean readYesNo(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            System.out.flush();
            String value = input.nextLine().trim();
            if (value.equalsIgnoreCase("c")) return true;
            if (value.equalsIgnoreCase("k")) return false;
            System.out.println("  Nhập c (có) hoặc k (không).");
        }
        return null;
    }

    private SanPhamYTe createMedicine(String id, String name, String categoryId, String manufacturer,
                                      double price, int quantity, LocalDate expiry, boolean requiresPrescription) {
        if (requiresPrescription) {
            return new ThuocKeDon(id, name, categoryId, manufacturer, price, quantity, expiry);
        }
        return new ThuocKhongKeDon(id, name, categoryId, manufacturer, price, quantity, expiry);
    }

    private void saveAllData() {
        try {
            luuTruDuLieu.save();
        } catch (IOException e) {
            throw new UncheckedIOException("Không thể lưu dữ liệu vào thư mục data.", e);
        }
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String money(double amount) {
        return String.format("%,.0f đ", amount);
    }
}
