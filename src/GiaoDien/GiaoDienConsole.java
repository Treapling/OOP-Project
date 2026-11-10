package GiaoDien;

import QuanLyChungTu.DonThuoc;
import QuanLyChungTu.InBaoCao;
import QuanLyChungTu.MaTuDong;
import QuanLyChungTu.PhieuNhap;
import QuanLyChungTu.PhieuXuat;
import QuanLyChungTu.QuanLyBanHang;
import QuanLyChungTu.QuanLyDonThuoc;
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
import QuanLySanPham.DonViTinh;
import QuanLySanPham.LoThuoc;
import QuanLySanPham.SanPhamYTe;
import QuanLySanPham.ThuocKeDon;
import QuanLySanPham.ThuocKhongKeDon;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GiaoDienConsole {
    private static final String LINE = "--------------------------------------------------------------------------";
    private final Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);
    private final QuanLyLoaiThuoc quanLyLoaiThuoc;
    private final DanhSachThuoc danhSachThuoc;
    private final QuanLyNhanVienKho quanLyNhanVien;
    private final QuanLyKhachHang quanLyKhachHang;
    private final QuanLyNhaCungCap quanLyNhaCungCap;
    private final QuanLyBanHang quanLyBanHang;
    private final QuanLyNhapKho quanLyNhapKho;
    private final QuanLyDonThuoc quanLyDonThuoc;
    private final Path dataDirectory;

    public GiaoDienConsole(
            QuanLyLoaiThuoc quanLyLoaiThuoc,
            DanhSachThuoc danhSachThuoc,
            QuanLyNhanVienKho quanLyNhanVien,
            QuanLyKhachHang quanLyKhachHang,
            QuanLyNhaCungCap quanLyNhaCungCap,
            QuanLyBanHang quanLyBanHang,
            QuanLyNhapKho quanLyNhapKho,
            QuanLyDonThuoc quanLyDonThuoc,
            Path dataDirectory
    ) {
        this.quanLyLoaiThuoc = quanLyLoaiThuoc;
        this.danhSachThuoc = danhSachThuoc;
        this.quanLyNhanVien = quanLyNhanVien;
        this.quanLyKhachHang = quanLyKhachHang;
        this.quanLyNhaCungCap = quanLyNhaCungCap;
        this.quanLyBanHang = quanLyBanHang;
        this.quanLyNhapKho = quanLyNhapKho;
        this.quanLyDonThuoc = quanLyDonThuoc;
        this.dataDirectory = dataDirectory;
    }

    public void run() {
        while (true) {
            printChoiceTable("QUẢN LÝ CỬA HÀNG THUỐC", "Chọn một chức năng để tiếp tục",
                    "1. Thuốc và tồn kho",
                    "2. Loại thuốc",
                    "3. Bán hàng",
                    "4. Nhập kho",
                    "5. Khách hàng, nhân viên, nhà cung cấp",
                    "6. Toa thuốc",
                    "7. Báo cáo doanh thu",
                    "8. Trạng thái file dữ liệu",
                    "0. Thoát");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) {
                System.out.println("\n  Đã thoát chương trình. Tạm biệt!");
                return;
            }
            if (choice < 0 || choice > 8) {
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
                case 8:
                    showDataFiles();
                    break;
                default:
                    System.out.println("  Không có lựa chọn này. Vui lòng chọn từ 0 đến 7.");
            }
        }
    }

    private void manageMedicines() {
        while (true) {
            printMedicines(danhSachThuoc.getAllMedicines(), "DANH SÁCH THUỐC");
            printSubmenu("1. Thêm thuốc", "2. Sửa thuốc", "3. Xóa thuốc",
                    "4. Thuốc/lô sắp hết hạn", "5. Tìm kiếm kết hợp",
                    "6. Cảnh báo tồn dưới định mức", "7. Xem các lô đang quản lý", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
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
                case 5: searchMedicine(); break;
                case 6: printLowStock(); break;
                case 7: manageBatches(); break;
                default: invalidChoice();
            }
        }
    }

    private void printMedicines(List<SanPhamYTe> medicines, String title) {
        System.out.println("\n  " + title);
        System.out.printf("  %-8s %-22s %-10s %12s %8s %8s %-12s %s%n",
                "Mã", "Tên thuốc", "Mã loại", "Giá/" + "đv gốc", "Tồn", "ĐM", "Hạn gần nhất", "Cần toa");
        System.out.println("  " + LINE);
        if (medicines.isEmpty()) {
            System.out.println("  Chưa có dữ liệu.");
        }
        for (SanPhamYTe medicine : medicines) {
            System.out.printf("  %-8s %-22s %-10s %12s %8d %8d %-12s %s%n",
                    medicine.getMedicineId(), medicine.getName(), medicine.getCategoryId(),
                    money(medicine.tinhGiaBan()), medicine.getQuantityInStock(), medicine.getReorderLevel(),
                    medicine.getExpiryDate(), medicine.isPrescriptionRequired() ? "Có" : "Không");
            System.out.println("           Đơn vị: " + medicine.getSellUnits().stream()
                    .map(unit -> unit.getName() + " (" + unit.getFactorToBase() + " "
                            + medicine.getBaseUnitName() + ")")
                    .collect(Collectors.joining(", ")));
        }
    }

    private void addMedicine() {
        String id = readText("  Mã thuốc: ");
        String name = readText("  Tên thuốc: ");
        String categoryId = readText("  Mã loại thuốc: ");
        String manufacturer = readText("  Nhà sản xuất: ");
        Double price = readDouble("  Giá bán: ");
        Integer quantity = readInt("  Số lượng tồn: ");
        LocalDate expiry = readDate("  Hạn dùng (yyyy-MM-dd): ");
        Boolean requiresPrescription = readYesNo("  Thuốc cần toa? (c/k): ");
        Integer reorderLevel = readInt("  Tồn kho tối thiểu: ");
        if (id == null || name == null || categoryId == null || manufacturer == null
                || price == null || quantity == null || expiry == null || requiresPrescription == null
                || reorderLevel == null) {
            return;
        }
        if (quanLyLoaiThuoc.getCategory(categoryId) == null) {
            System.out.println("  Không tìm thấy loại thuốc này.");
            return;
        }
        if (danhSachThuoc.getMedicine(id) != null) {
            System.out.println("  Mã thuốc đã tồn tại.");
            return;
        }
        if (price < 0 || quantity < 0 || reorderLevel < 0) {
            System.out.println("  Giá, số lượng và định mức không được âm.");
            return;
        }
        SanPhamYTe medicine = createMedicine(id, name, categoryId, manufacturer,
                price, quantity, expiry, requiresPrescription);
        if (!configureUnits(medicine, null, reorderLevel)) return;
        boolean added = danhSachThuoc.addMedicine(medicine);
        System.out.println(added ? "  Đã thêm thuốc." : "  Không thể thêm thuốc. Hãy kiểm tra mã thuốc.");
    }

    private void updateMedicine() {
        String id = readText("  Nhập mã thuốc cần sửa: ");
        if (id == null) return;
        SanPhamYTe current = danhSachThuoc.getMedicine(id);
        if (current == null) {
            System.out.println("  Không tìm thấy thuốc.");
            return;
        }
        String name = readText("  Tên thuốc mới: ");
        String categoryId = readText("  Mã loại mới: ");
        String manufacturer = readText("  Nhà sản xuất mới: ");
        Double price = readDouble("  Giá bán mới: ");
        LocalDate expiry;
        if (danhSachThuoc.hasBatchTracking()) {
            expiry = current.getExpiryDate();
            System.out.println("  Hạn dùng do từng lô quyết định; chỉnh hạn dùng trong bước nhập lô.");
        } else {
            expiry = readDate("  Hạn dùng mới (yyyy-MM-dd): ");
        }
        Boolean requiresPrescription = readYesNo("  Cần toa? (c/k): ");
        Integer reorderLevel = readInt("  Tồn kho tối thiểu mới: ");
        if (name == null || categoryId == null || manufacturer == null || price == null
                || requiresPrescription == null || reorderLevel == null) return;
        if (quanLyLoaiThuoc.getCategory(categoryId) == null || price < 0 || reorderLevel < 0) {
            System.out.println("  Mã loại không tồn tại, hoặc giá/định mức không hợp lệ.");
            return;
        }
        SanPhamYTe updated = createMedicine(id, name, categoryId, manufacturer,
                price, current.getQuantityInStock(), expiry, requiresPrescription);
        if (!configureUnits(updated, current, reorderLevel)) return;
        boolean saved = danhSachThuoc.updateMedicine(updated);
        System.out.println(saved ? "  Đã cập nhật thuốc."
                : "  Cập nhật thất bại. Tồn kho được điều chỉnh qua nhập kho, bán hàng hoặc lô.");
    }

    private void deleteMedicine() {
        String id = readText("  Nhập mã thuốc cần xóa: ");
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
        System.out.println(deleted ? "  Đã xóa thuốc." : "  Không tìm thấy thuốc.");
    }

    private boolean configureUnits(SanPhamYTe medicine, SanPhamYTe existing, int reorderLevel) {
        String base = readText("  Đơn vị nhỏ nhất (viên/chai/miếng...): ");
        String middle = readOptionalText("  Đơn vị trung gian (bỏ trống nếu không có): ");
        if (base == null || middle == null) return false;
        int middleFactor = 1;
        if (!middle.isBlank()) {
            Integer factor = readInt("  Số đơn vị nhỏ nhất trong 1 " + middle + ": ");
            if (factor == null) return false;
            middleFactor = factor;
        }
        String outer = readOptionalText("  Đơn vị đóng gói lớn nhất (bỏ trống nếu không có): ");
        if (outer == null) return false;
        int outerFactor = 1;
        if (!outer.isBlank()) {
            if (middle.isBlank()) {
                System.out.println("  Cần khai báo đơn vị trung gian trước đơn vị đóng gói lớn nhất.");
                return false;
            }
            Integer factor = readInt("  Số " + middle + " trong 1 " + outer + ": ");
            if (factor == null) return false;
            outerFactor = factor;
        }
        try {
            medicine.setUnitConfiguration(base, middle, middleFactor, outer, outerFactor);
        } catch (IllegalArgumentException e) {
            System.out.println("  Quy cách đơn vị không hợp lệ.");
            return false;
        }
        if (existing != null && existing.getQuantityInStock() > 0
                && (!existing.getBaseUnitName().equals(medicine.getBaseUnitName())
                    || !existing.getMiddleUnitName().equals(medicine.getMiddleUnitName())
                    || existing.getMiddleToBaseFactor() != medicine.getMiddleToBaseFactor()
                    || !existing.getOuterUnitName().equals(medicine.getOuterUnitName())
                    || existing.getOuterToMiddleFactor() != medicine.getOuterToMiddleFactor())) {
            System.out.println("  Không thể đổi quy cách khi còn tồn; cần quy đổi/kiểm kê tồn kho trước.");
            return false;
        }
        medicine.setReorderLevel(reorderLevel);
        return true;
    }

    private void searchMedicine() {
        String keyword = readOptionalText("  Từ khóa gần đúng (mã/tên/hãng): ");
        String category = readOptionalText("  Lọc theo mã loại (bỏ trống nếu không): ");
        String manufacturer = readOptionalText("  Lọc theo hãng (bỏ trống nếu không): ");
        if (keyword == null || category == null || manufacturer == null) return;
        printMedicines(danhSachThuoc.searchMedicines(keyword, category, manufacturer, null, false),
                "KẾT QUẢ TÌM KIẾM");
    }

    private void printLowStock() {
        List<SanPhamYTe> lowStock = danhSachThuoc.getLowStockMedicines();
        printMedicines(lowStock, "ĐỀ XUẤT BỔ SUNG TỒN KHO");
        for (SanPhamYTe medicine : lowStock) {
            long suggested = Math.max(0L, (long) medicine.getReorderLevel() * 2
                    - danhSachThuoc.getAvailableStock(medicine.getMedicineId()));
            System.out.println("  Gợi ý: lập phiếu nhập cho " + medicine.getMedicineId()
                    + ", nhập khoảng " + suggested + " " + medicine.getBaseUnitName()
                    + " để phục hồi mức mục tiêu 2 x định mức.");
        }
    }

    private void manageBatches() {
        while (true) {
            printBatches();
            printSubmenu("1. Thu hồi/cách ly lô", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            if (choice == 1) {
                String batchNumber = readText("  Số lô cần thu hồi: ");
                if (batchNumber != null) {
                    System.out.println(danhSachThuoc.recallBatch(batchNumber)
                            ? "  Đã cách ly lô khỏi bán hàng."
                            : "  Không tìm thấy lô hoặc lô đã được thu hồi.");
                }
            } else {
                invalidChoice();
            }
        }
    }

    private void printBatches() {
        System.out.println("\n  THEO DÕI LÔ THUỐC (FEFO: xuất lô hết hạn trước)");
        System.out.printf("  %-10s %-20s %-26s %-12s %10s %s%n",
                "Mã thuốc", "Số lô", "Tên thuốc", "Hạn dùng", "Tồn", "Trạng thái");
        System.out.println("  " + LINE);
        for (LoThuoc batch : danhSachThuoc.getAllBatches()) {
            SanPhamYTe medicine = danhSachThuoc.getMedicine(batch.getMedicineId());
            String status = batch.isRecalled() ? "ĐÃ THU HỒI"
                    : batch.getExpiryDate().isBefore(LocalDate.now()) ? "ĐÃ HẾT HẠN"
                    : batch.getExpiryDate().isBefore(LocalDate.now().plusDays(90)) ? "Sắp hết hạn" : "Còn hạn";
            System.out.printf("  %-10s %-20s %-26s %-12s %10d %s%n",
                    batch.getMedicineId(), batch.getBatchNumber(), medicine.getName(),
                    batch.getExpiryDate(), batch.getQuantity(), status);
        }
    }

    private void manageCategories() {
        while (true) {
            printCategories();
            printSubmenu("1. Thêm loại thuốc", "2. Sửa loại thuốc", "3. Xóa loại thuốc", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addCategory(); break;
                case 2: updateCategory(); break;
                case 3: deleteCategory(); break;
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

    private void addCategory() {
        String id = readText("  Mã loại: ");
        String name = readText("  Tên loại: ");
        String description = readText("  Mô tả: ");
        if (id == null || name == null || description == null) {
            return;
        }
        if (quanLyLoaiThuoc.getCategory(id) != null) {
            System.out.println("  Mã loại thuốc đã tồn tại.");
            return;
        }
        boolean added = quanLyLoaiThuoc.addCategory(new LoaiThuoc(id, name, description));
        System.out.println(added ? "  Đã thêm loại thuốc." : "  Không thể thêm loại thuốc.");
    }

    private void updateCategory() {
        String id = readText("  Mã loại cần sửa: ");
        String name = readText("  Tên loại mới: ");
        String description = readText("  Mô tả mới: ");
        if (id == null || name == null || description == null) return;
        System.out.println(quanLyLoaiThuoc.updateCategory(id, name, description)
                ? "  Đã cập nhật loại thuốc." : "  Không tìm thấy loại thuốc.");
    }

    private void deleteCategory() {
        String id = readText("  Mã loại cần xóa: ");
        if (id == null) return;
        for (SanPhamYTe medicine : danhSachThuoc.getAllMedicines()) {
            if (id.equals(medicine.getCategoryId())) {
                System.out.println("  Không thể xóa loại đang được thuốc sử dụng.");
                return;
            }
        }
        System.out.println(quanLyLoaiThuoc.deleteCategory(id)
                ? "  Đã xóa loại thuốc." : "  Không tìm thấy loại thuốc.");
    }

    private void sellMedicine() {
        while (true) {
            printInvoices();
            printSubmenu("1. Tạo hóa đơn bán thuốc", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            if (choice == 1) createSale();
            else invalidChoice();
        }
    }

    private void printInvoices() {
        System.out.println("\n  DANH SÁCH HÓA ĐƠN");
        System.out.printf("  %-18s %-10s %-10s %-12s %14s %-16s%n",
                "Mã hóa đơn", "Khách hàng", "Nhân viên", "Ngày tạo", "Tổng tiền", "Trạng thái");
        System.out.println("  " + LINE);
        List<PhieuXuat> invoices = quanLyBanHang.getAllInvoices();
        if (invoices.isEmpty()) System.out.println("  Chưa có hóa đơn.");
        for (PhieuXuat invoice : invoices) {
            System.out.printf("  %-18s %-10s %-10s %-12s %14s %-16s%n",
                    invoice.getInvoiceId(), invoice.getCustomerId(), invoice.getEmployeeId(),
                    invoice.getCreatedAt().toLocalDate(), money(invoice.getTotalAmount()),
                    invoice.isPaid() ? "Đã thanh toán" : "Chưa thanh toán");
        }
    }

    private void createSale() {
        String medicineId = readText("  Mã thuốc: ");
        String customerId = readText("  Mã khách hàng: ");
        String employeeId = readText("  Mã nhân viên: ");
        if (medicineId == null || customerId == null || employeeId == null) {
            return;
        }
        SanPhamYTe medicine = danhSachThuoc.getMedicine(medicineId);
        if (medicine == null || quanLyKhachHang.getCustomer(customerId) == null
                || quanLyNhanVien.getEmployee(employeeId) == null) {
            System.out.println("  Không tìm thấy thuốc, khách hàng hoặc nhân viên.");
            return;
        }
        List<DonViTinh> sellUnits = medicine.getSellUnits();
        System.out.println("  Đơn vị bán:");
        for (int i = 0; i < sellUnits.size(); i++) {
            DonViTinh unit = sellUnits.get(i);
            System.out.println("  " + (i + 1) + ". " + unit.getName() + " = " + unit.getFactorToBase()
                    + " " + medicine.getBaseUnitName() + ", giá "
                    + money(medicine.tinhGiaBan() * unit.getFactorToBase()));
        }
        Integer unitIndex = readInt("  Chọn đơn vị bán: ");
        Integer quantity = readInt("  Số lượng theo đơn vị đã chọn: ");
        String payment = readText("  Phương thức thanh toán: ");
        if (unitIndex == null || quantity == null || payment == null) return;
        if (unitIndex < 1 || unitIndex > sellUnits.size() || quantity <= 0) {
            System.out.println("  Đơn vị hoặc số lượng không hợp lệ.");
            return;
        }
        DonViTinh selectedUnit = sellUnits.get(unitIndex - 1);
        int baseQuantity;
        try {
            baseQuantity = Math.multiplyExact(quantity, selectedUnit.getFactorToBase());
        } catch (ArithmeticException e) {
            System.out.println("  Số lượng quy đổi vượt giới hạn.");
            return;
        }
        if (medicine.getQuantityInStock() < baseQuantity) {
            System.out.println("  Tồn kho không đủ theo đơn vị nhỏ nhất.");
            return;
        }
        if (medicine.isPrescriptionRequired()) {
            String prescriptionId = readText("  Mã toa thuốc: ");
            if (prescriptionId == null
                    || !quanLyDonThuoc.isMedicineIncluded(prescriptionId, medicineId)) {
                System.out.println("  Thuốc này cần toa hợp lệ có chứa thuốc.");
                return;
            }
        }
        String invoiceId = MaTuDong.taoMa("HD");
        PhieuXuat invoice = quanLyBanHang.createInvoice(invoiceId, customerId, employeeId, payment);
        if (!quanLyBanHang.addMedicineToInvoice(invoiceId, medicineId, quantity, selectedUnit.getName())) {
            System.out.println("  Bán thất bại. Kiểm tra hạn dùng từng lô và tồn kho khả dụng.");
            return;
        }
        quanLyBanHang.finalizeInvoice(invoiceId);
        System.out.println("  Tạo hóa đơn thành công: " + invoiceId);
        System.out.println("  Tổng thanh toán: " + money(invoice.getTotalAmount()));
    }

    private void importStock() {
        while (true) {
            printImportNotes();
            printSubmenu("1. Tạo phiếu nhập", "2. Nhập thêm thuốc", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            if (choice == 1) createImportNote();
            else if (choice == 2) importMedicineStock();
            else invalidChoice();
        }
    }

    private void printImportNotes() {
        System.out.println("\n  PHIẾU NHẬP KHO");
        System.out.printf("  %-12s %-12s %-12s %-12s %14s %s%n",
                "Mã phiếu", "Nhà cung cấp", "Nhân viên", "Ngày nhập", "Tổng tiền", "Mã thuốc");
        System.out.println("  " + LINE);
        if (quanLyNhapKho.getAllImportNotes().isEmpty()) System.out.println("  Chưa có phiếu nhập.");
        for (PhieuNhap note : quanLyNhapKho.getAllImportNotes()) {
            System.out.printf("  %-12s %-12s %-12s %-12s %14s %s%n",
                    note.getImportNoteId(), note.getSupplierId(), note.getEmployeeId(),
                    note.getImportDate(), money(note.getTotalAmount()),
                    note.getDetails().stream()
                            .map(detail -> detail.getMedicineId() + " [" + detail.getBatchNumber() + "] x"
                                    + detail.getQuantity() + " HSD " + detail.getExpiryDate()
                                    + " @" + money(detail.getUnitPrice()))
                            .collect(Collectors.joining(", ")));
        }
    }

    private void importMedicineStock() {
        String noteId = readText("  Mã phiếu nhập: ");
        String medicineId = readText("  Mã thuốc: ");
        String batchNumber = readText("  Số lô nhà sản xuất: ");
        LocalDate expiry = readDate("  Hạn dùng của lô (yyyy-MM-dd): ");
        Integer quantity = readInt("  Số lượng nhập: ");
        Double price = readDouble("  Giá nhập mỗi đơn vị nhỏ nhất: ");
        if (noteId == null || medicineId == null || batchNumber == null || expiry == null
                || quantity == null || price == null) return;
        if (quantity <= 0 || price < 0) {
            System.out.println("  Số lượng phải lớn hơn 0 và giá nhập không được âm.");
            return;
        }
        if (quanLyNhapKho.getImportNote(noteId) == null || danhSachThuoc.getMedicine(medicineId) == null) {
            System.out.println("  Không tìm thấy phiếu nhập hoặc thuốc.");
            return;
        }
        boolean imported = quanLyNhapKho.importMedicine(noteId, medicineId, batchNumber, expiry, quantity, price);
        System.out.println(imported
                ? "  Nhập kho thành công."
                : "  Nhập kho thất bại. Kiểm tra mã phiếu và mã thuốc.");
    }

    private void createImportNote() {
        String id = readOptionalText("  Mã phiếu nhập (bỏ trống để tự tạo): ");
        String supplierId = readText("  Mã nhà cung cấp: ");
        String employeeId = readText("  Mã nhân viên: ");
        if (id == null || supplierId == null || employeeId == null) return;
        if (id.isBlank()) id = MaTuDong.taoMa("PN");
        if (quanLyNhapKho.getImportNote(id) != null) {
            System.out.println("  Mã phiếu nhập đã tồn tại.");
            return;
        }
        if (quanLyNhaCungCap.getSupplier(supplierId) == null
                || quanLyNhanVien.getEmployee(employeeId) == null) {
            System.out.println("  Không tìm thấy nhà cung cấp hoặc nhân viên.");
            return;
        }
        boolean created = quanLyNhapKho.addImportNote(new PhieuNhap(id, supplierId, employeeId, 0));
        System.out.println(created ? "  Đã tạo phiếu nhập." : "  Không thể tạo phiếu nhập.");
    }

    private void managePartners() {
        while (true) {
            System.out.println("\n  QUẢN LÝ ĐỐI TÁC");
            printSubmenu("1. Khách hàng", "2. Nhân viên", "3. Nhà cung cấp", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
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
            printSubmenu("1. Thêm khách hàng", "2. Sửa khách hàng", "3. Xóa khách hàng", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addCustomer(); break;
                case 2: updateCustomer(); break;
                case 3: deleteCustomer(); break;
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

    private void addCustomer() {
        String id = readText("  Mã khách hàng: ");
        String name = readText("  Họ tên: ");
        String phone = readText("  Số điện thoại: ");
        if (id != null && name != null && phone != null) {
            if (quanLyKhachHang.getCustomer(id) != null) {
                System.out.println("  Mã khách hàng đã tồn tại.");
                return;
            }
            boolean added = quanLyKhachHang.addCustomer(new KhachHang(id, name, phone, 0));
            System.out.println(added ? "  Đã thêm khách hàng." : "  Không thể thêm khách hàng.");
        }
    }

    private void updateCustomer() {
        String id = readText("  Mã khách hàng cần sửa: ");
        if (id == null) return;
        KhachHang customer = quanLyKhachHang.getCustomer(id);
        if (customer == null) {
            System.out.println("  Không tìm thấy khách hàng.");
            return;
        }
        String name = readText("  Họ tên mới: ");
        String phone = readText("  Số điện thoại mới: ");
        if (name == null || phone == null) return;
        customer.setName(name);
        customer.setPhone(phone);
        System.out.println(quanLyKhachHang.updateCustomer(customer) ? "  Đã cập nhật khách hàng." : "  Cập nhật thất bại.");
    }

    private void deleteCustomer() {
        String id = readText("  Mã khách hàng cần xóa: ");
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
        System.out.println(quanLyKhachHang.deleteCustomer(id)
                ? "  Đã xóa khách hàng." : "  Không tìm thấy khách hàng.");
    }

    private void manageEmployees() {
        while (true) {
            printEmployees();
            printSubmenu("1. Thêm nhân viên", "2. Sửa nhân viên", "3. Xóa nhân viên", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addEmployee(); break;
                case 2: updateEmployee(); break;
                case 3: deleteEmployee(); break;
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

    private void addEmployee() {
        String id = readText("  Mã nhân viên: ");
        String name = readText("  Họ tên: ");
        String role = readText("  Chức vụ: ");
        String phone = readText("  Số điện thoại: ");
        Double salary = readDouble("  Lương cơ bản: ");
        if (id != null && name != null && role != null && phone != null && salary != null) {
            if (quanLyNhanVien.getEmployee(id) != null || salary < 0) {
                System.out.println("  Mã nhân viên đã tồn tại hoặc lương không hợp lệ.");
                return;
            }
            boolean added = quanLyNhanVien.addEmployee(new NhanVienKho(id, name, role, phone, salary, 0));
            System.out.println(added ? "  Đã thêm nhân viên." : "  Không thể thêm nhân viên.");
        }
    }

    private void updateEmployee() {
        String id = readText("  Mã nhân viên cần sửa: ");
        if (id == null) return;
        NhanVienKho current = quanLyNhanVien.getEmployee(id);
        if (current == null) {
            System.out.println("  Không tìm thấy nhân viên.");
            return;
        }
        String name = readText("  Họ tên mới: ");
        String role = readText("  Chức vụ mới: ");
        String phone = readText("  Số điện thoại mới: ");
        Double salary = readDouble("  Lương cơ bản mới: ");
        Double commission = readDouble("  Hoa hồng mới: ");
        if (name == null || role == null || phone == null || salary == null || commission == null) return;
        if (salary < 0 || commission < 0) {
            System.out.println("  Lương và hoa hồng không được âm.");
            return;
        }
        NhanVienKho updated = new NhanVienKho(id, name, role, phone, salary, commission);
        System.out.println(quanLyNhanVien.updateEmployee(updated) ? "  Đã cập nhật nhân viên." : "  Cập nhật thất bại.");
    }

    private void deleteEmployee() {
        String id = readText("  Mã nhân viên cần xóa: ");
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
        System.out.println(quanLyNhanVien.deleteEmployee(id)
                ? "  Đã xóa nhân viên." : "  Không tìm thấy nhân viên.");
    }

    private void manageSuppliers() {
        while (true) {
            printSuppliers();
            printSubmenu("1. Thêm nhà cung cấp", "2. Sửa nhà cung cấp", "3. Xóa nhà cung cấp", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1: addSupplier(); break;
                case 2: updateSupplier(); break;
                case 3: deleteSupplier(); break;
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

    private void addSupplier() {
        String id = readText("  Mã nhà cung cấp: ");
        String name = readText("  Tên nhà cung cấp: ");
        String phone = readText("  Số điện thoại: ");
        String address = readText("  Địa chỉ: ");
        if (id != null && name != null && phone != null && address != null) {
            if (quanLyNhaCungCap.getSupplier(id) != null) {
                System.out.println("  Mã nhà cung cấp đã tồn tại.");
                return;
            }
            boolean added = quanLyNhaCungCap.addSupplier(new NhaCungCap(id, name, phone, address));
            System.out.println(added ? "  Đã thêm nhà cung cấp." : "  Không thể thêm nhà cung cấp.");
        }
    }

    private void updateSupplier() {
        String id = readText("  Mã nhà cung cấp cần sửa: ");
        if (id == null) return;
        if (quanLyNhaCungCap.getAllSuppliers().stream().noneMatch(s -> id.equals(s.getSupplierId()))) {
            System.out.println("  Không tìm thấy nhà cung cấp.");
            return;
        }
        String name = readText("  Tên mới: ");
        String phone = readText("  Số điện thoại mới: ");
        String address = readText("  Địa chỉ mới: ");
        if (name == null || phone == null || address == null) return;
        System.out.println(quanLyNhaCungCap.updateSupplier(new NhaCungCap(id, name, phone, address))
                ? "  Đã cập nhật nhà cung cấp." : "  Cập nhật thất bại.");
    }

    private void deleteSupplier() {
        String id = readText("  Mã nhà cung cấp cần xóa: ");
        if (id == null) return;
        boolean usedByImport = quanLyNhapKho.getAllImportNotes().stream()
                .anyMatch(note -> id.equals(note.getSupplierId()));
        if (usedByImport) {
            System.out.println("  Không thể xóa nhà cung cấp đã có phiếu nhập.");
            return;
        }
        System.out.println(quanLyNhaCungCap.deleteSupplier(id)
                ? "  Đã xóa nhà cung cấp." : "  Không tìm thấy nhà cung cấp.");
    }

    private void managePrescriptions() {
        while (true) {
            printPrescriptions();
            printSubmenu("1. Tạo toa thuốc", "2. Kiểm tra thuốc trong toa", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
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
            } else {
                invalidChoice();
            }
        }
    }

    private void printPrescriptions() {
        System.out.println("\n  DANH SÁCH TOA THUỐC");
        System.out.printf("  %-12s %-22s %-12s %-20s %-12s %s%n",
                "Mã toa", "Bệnh nhân", "Mã khách", "Bác sĩ", "Ngày kê", "Mã thuốc");
        System.out.println("  " + LINE);
        if (quanLyDonThuoc.getAllPrescriptions().isEmpty()) System.out.println("  Chưa có toa thuốc.");
        for (DonThuoc prescription : quanLyDonThuoc.getAllPrescriptions()) {
            System.out.printf("  %-12s %-22s %-12s %-20s %-12s %s%n",
                    prescription.getPrescriptionId(), prescription.getPatientName(),
                    prescription.getCustomerId(), prescription.getDoctorName(),
                    prescription.getIssueDate(), String.join(", ", prescription.getMedicineIds()));
        }
    }

    private void addPrescription() {
        String id = readOptionalText("  Mã toa (bỏ trống để tự tạo): ");
        String customerId = readText("  Mã khách hàng: ");
        String patient = readText("  Tên bệnh nhân: ");
        String doctor = readText("  Tên bác sĩ: ");
        String medicineId = readText("  Mã thuốc: ");
        if (id == null || customerId == null || patient == null || doctor == null || medicineId == null) {
            return;
        }
        if (id.isBlank()) id = MaTuDong.taoMa("TOA");
        if (quanLyKhachHang.getCustomer(customerId) == null || danhSachThuoc.getMedicine(medicineId) == null) {
            System.out.println("  Không tìm thấy khách hàng hoặc thuốc.");
            return;
        }
        if (quanLyDonThuoc.getPrescription(id) != null) {
            System.out.println("  Mã toa thuốc đã tồn tại.");
            return;
        }
        DonThuoc prescription = new DonThuoc(id, customerId, patient, doctor, LocalDate.now());
        prescription.addMedicine(medicineId);
        boolean added = quanLyDonThuoc.addPrescription(prescription);
        System.out.println(added ? "  Đã tạo toa thuốc." : "  Không thể tạo toa thuốc.");
    }

    private void showRevenueReport() {
        while (true) {
            System.out.println("\n  BÁO CÁO");
            printSubmenu("1. Doanh thu hôm nay", "2. Doanh thu theo tháng",
                    "3. Thuốc/lô sắp hết hạn", "4. Thống kê kho đa chiều", "0. Quay lại");
            Integer choice = readInt("  Lựa chọn: ");
            if (choice == null || choice == 0) return;
            InBaoCao report = new InBaoCao(quanLyBanHang.getAllInvoices());
            if (choice == 1) {
                LocalDate today = LocalDate.now();
                System.out.println("  Doanh thu ngày " + today + ": "
                        + money(report.getDailyRevenue(today)));
            } else if (choice == 2) {
                Integer year = readInt("  Năm: ");
                Integer month = readInt("  Tháng (1-12): ");
                if (year != null && month != null && month >= 1 && month <= 12) {
                    System.out.println("  Doanh thu tháng " + month + "/" + year + ": "
                            + money(report.getMonthlyRevenue(year, month)));
                } else if (month != null) {
                    System.out.println("  Tháng không hợp lệ.");
                }
            } else if (choice == 3) {
                Integer days = readInt("  Số ngày tới hạn cần xem: ");
                if (days != null && days >= 0) {
                    printMedicines(danhSachThuoc.filterExpiringSoon(days), "THUỐC SẮP HẾT HẠN");
                    for (LoThuoc batch : danhSachThuoc.filterExpiringBatches(days)) {
                        System.out.println("  Lô " + batch.getBatchNumber() + " / "
                                + batch.getMedicineId() + " / " + batch.getExpiryDate()
                                + " / tồn " + batch.getQuantity());
                    }
                } else if (days != null) {
                    System.out.println("  Số ngày phải lớn hơn hoặc bằng 0.");
                }
            } else if (choice == 4) {
                showInventoryStatistics();
            } else {
                invalidChoice();
            }
        }
    }

    private void showInventoryStatistics() {
        Map<String, long[]> byCategory = new LinkedHashMap<>();
        long prescriptionCount = 0;
        long otcCount = 0;
        long totalUnits = 0;
        double stockValue = 0;
        for (SanPhamYTe medicine : danhSachThuoc.getAllMedicines()) {
            long[] totals = byCategory.computeIfAbsent(medicine.getCategoryId(), ignored -> new long[2]);
            totals[0] += medicine.getQuantityInStock();
            totals[1]++;
            totalUnits += medicine.getQuantityInStock();
            stockValue += medicine.getQuantityInStock() * medicine.getUnitPrice();
            if (medicine.isPrescriptionRequired()) prescriptionCount++;
            else otcCount++;
        }
        System.out.println("\n  THỐNG KÊ TỒN KHO");
        System.out.println("  Số mặt hàng: " + danhSachThuoc.getAllMedicines().size()
                + " | Tổng tồn theo đơn vị gốc: " + totalUnits
                + " | Giá trị tồn theo giá nhập/đơn vị gốc: " + money(stockValue));
        System.out.println("  Thuốc kê đơn: " + prescriptionCount + " | Không kê đơn/vật tư: " + otcCount);
        System.out.println("  Theo loại thuốc:");
        for (Map.Entry<String, long[]> entry : byCategory.entrySet()) {
            LoaiThuoc category = quanLyLoaiThuoc.getCategory(entry.getKey());
            System.out.println("  - " + entry.getKey() + " (" + (category == null ? "chưa phân loại" : category.getName())
                    + "): " + entry.getValue()[1] + " mặt hàng, tồn " + entry.getValue()[0]);
        }
        long expiredUnits = danhSachThuoc.filterExpiringBatches(0).stream()
                .filter(batch -> batch.getExpiryDate().isBefore(LocalDate.now()))
                .mapToLong(LoThuoc::getQuantity).sum();
        long expiringUnits = danhSachThuoc.filterExpiringBatches(90).stream()
                .filter(batch -> !batch.getExpiryDate().isBefore(LocalDate.now()))
                .mapToLong(LoThuoc::getQuantity).sum();
        System.out.println("  Tồn đã hết hạn: " + expiredUnits
                + " | Tồn hết hạn trong 90 ngày: " + expiringUnits
                + " | Mặt hàng dưới định mức: " + danhSachThuoc.getLowStockMedicines().size());
    }

    private void showDataFiles() {
        String[] files = {"thuoc.txt", "lo-thuoc.txt", "loai-thuoc.txt", "khach-hang.txt",
                "nhan-vien.txt", "nha-cung-cap.txt", "hoa-don.txt", "phieu-nhap.txt", "toa-thuoc.txt"};
        System.out.println("\n  FILE DỮ LIỆU ĐANG ĐƯỢC ỨNG DỤNG SỬ DỤNG");
        System.out.println("  Thư mục: " + dataDirectory.toAbsolutePath().normalize());
        for (String fileName : files) {
            Path file = dataDirectory.resolve(fileName);
            if (!Files.isRegularFile(file)) {
                System.out.println("  [THIẾU] " + fileName + " -> " + file.toAbsolutePath());
                continue;
            }
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                long rows = reader.lines().filter(line -> !line.isBlank()).count();
                System.out.println("  [OK] " + fileName + " | " + rows + " bản ghi | " + Files.size(file)
                        + " bytes | " + file.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("  [LỖI ĐỌC] " + file.toAbsolutePath() + " | " + e.getMessage());
            }
        }
        System.out.println("  Đây là file văn bản TSV, không phải database SQL; mở bằng editor để xem trường chi tiết.");
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
        System.out.print(prompt);
        if (!input.hasNextLine()) {
            return null;
        }
        String value = input.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("  Giá trị không được để trống.");
            return null;
        }
        return value;
    }

    private String readOptionalText(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) return null;
        return input.nextLine().trim();
    }

    private Integer readInt(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số nguyên.");
            }
        }
        return null;
    }

    private Double readDouble(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(input.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("  Vui lòng nhập số hợp lệ.");
            }
        }
        return null;
    }

    private LocalDate readDate(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(input.nextLine().trim());
            } catch (DateTimeParseException exception) {
                System.out.println("  Ngày không hợp lệ. Dùng định dạng yyyy-MM-dd.");
            }
        }
        return null;
    }

    private Boolean readYesNo(String prompt) {
        while (input.hasNextLine()) {
            System.out.print(prompt);
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

    private String money(double amount) {
        return String.format("%,.0f đ", amount);
    }
}
