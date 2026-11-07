package QuanLyChungTu;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LuuTruDuLieu {
    private final Path dataDirectory;
    private final QuanLyLoaiThuoc quanLyLoaiThuoc;
    private final DanhSachThuoc danhSachThuoc;
    private final QuanLyKhachHang quanLyKhachHang;
    private final QuanLyNhanVienKho quanLyNhanVien;
    private final QuanLyNhaCungCap quanLyNhaCungCap;
    private final QuanLyBanHang quanLyBanHang;
    private final QuanLyNhapKho quanLyNhapKho;
    private final QuanLyDonThuoc quanLyDonThuoc;

    public LuuTruDuLieu(
            Path dataDirectory,
            QuanLyLoaiThuoc quanLyLoaiThuoc,
            DanhSachThuoc danhSachThuoc,
            QuanLyKhachHang quanLyKhachHang,
            QuanLyNhanVienKho quanLyNhanVien,
            QuanLyNhaCungCap quanLyNhaCungCap,
            QuanLyBanHang quanLyBanHang,
            QuanLyNhapKho quanLyNhapKho,
            QuanLyDonThuoc quanLyDonThuoc
    ) {
        this.dataDirectory = dataDirectory;
        this.quanLyLoaiThuoc = quanLyLoaiThuoc;
        this.danhSachThuoc = danhSachThuoc;
        this.quanLyKhachHang = quanLyKhachHang;
        this.quanLyNhanVien = quanLyNhanVien;
        this.quanLyNhaCungCap = quanLyNhaCungCap;
        this.quanLyBanHang = quanLyBanHang;
        this.quanLyNhapKho = quanLyNhapKho;
        this.quanLyDonThuoc = quanLyDonThuoc;
    }

    public void load() throws IOException {
        quanLyLoaiThuoc.replaceAllCategories(loadCategories());
        quanLyKhachHang.replaceAllCustomers(loadCustomers());
        quanLyNhanVien.replaceAllEmployees(loadEmployees());
        quanLyNhaCungCap.replaceAllSuppliers(loadSuppliers());
        quanLyDonThuoc.replaceAllPrescriptions(loadPrescriptions());
        quanLyNhapKho.replaceAllImportNotes(loadImportNotes());
        loadInvoices();
    }

    public void save() throws IOException {
        Files.createDirectories(dataDirectory);
        danhSachThuoc.ghiFile(dataDirectory.resolve("thuoc.txt").toString());
        writeRows("loai_thuoc.txt", "ma_loai\tten\temo_ta", categoryRows());
        writeRows("khach_hang.txt", "ma_khach\tten\tdien_thoai\tdiem\thang_hang", customerRows());
        writeRows("nhan_vien_kho.txt", "ma_nhan_vien\tten\tchuc_vu\tdien_thoai\tluong_co_ban\thoa_hong",
                employeeRows());
        writeRows("nha_cung_cap.txt", "ma_nha_cung_cap\tten\tdien_thoai\tdia_chi", supplierRows());
        writeRows("toa_thuoc.txt", "ma_toa\tma_khach\tten_benh_nhan\tbac_si\tngay_ke", prescriptionRows());
        writeRows("toa_thuoc_chi_tiet.txt", "ma_toa\tma_thuoc", prescriptionMedicineRows());
        writeRows("phieu_nhap.txt", "ma_phieu\tnha_cung_cap\tnhan_vien\tngay_nhap\ttong_tien", importRows());
        writeRows("phieu_nhap_chi_tiet.txt", "ma_phieu\tma_thuoc", importMedicineRows());
        writeRows("hoa_don.txt",
                "ma_hoa_don\tma_khach\tnhan_vien\tthoi_gian\ttong_tien\tphuong_thuc\tda_thanh_toan",
                invoiceRows());
        writeRows("hoa_don_chi_tiet.txt", "ma_chi_tiet\tma_hoa_don\tma_thuoc\tso_luong\tgia_ban",
                invoiceDetailRows());
    }

    private List<LoaiThuoc> loadCategories() throws IOException {
        List<LoaiThuoc> result = new ArrayList<>();
        for (List<String> row : readRows("loai_thuoc.txt", 3)) {
            result.add(new LoaiThuoc(row.get(0), row.get(1), row.get(2)));
        }
        return result;
    }

    private List<KhachHang> loadCustomers() throws IOException {
        List<KhachHang> result = new ArrayList<>();
        for (List<String> row : readRows("khach_hang.txt", 5)) {
            try {
                KhachHang customer = new KhachHang(row.get(0), row.get(1), row.get(2),
                        Integer.parseInt(row.get(3)));
                customer.setMemberTier(row.get(4));
                result.add(customer);
            } catch (RuntimeException exception) {
                throw invalidData("khach_hang.txt", exception);
            }
        }
        return result;
    }

    private List<NhanVienKho> loadEmployees() throws IOException {
        List<NhanVienKho> result = new ArrayList<>();
        for (List<String> row : readRows("nhan_vien_kho.txt", 6)) {
            try {
                result.add(new NhanVienKho(row.get(0), row.get(1), row.get(2), row.get(3),
                        Double.parseDouble(row.get(4)), Double.parseDouble(row.get(5))));
            } catch (RuntimeException exception) {
                throw invalidData("nhan_vien_kho.txt", exception);
            }
        }
        return result;
    }

    private List<NhaCungCap> loadSuppliers() throws IOException {
        List<NhaCungCap> result = new ArrayList<>();
        for (List<String> row : readRows("nha_cung_cap.txt", 4)) {
            result.add(new NhaCungCap(row.get(0), row.get(1), row.get(2), row.get(3)));
        }
        return result;
    }

    private List<DonThuoc> loadPrescriptions() throws IOException {
        Map<String, DonThuoc> prescriptions = new LinkedHashMap<>();
        for (List<String> row : readRows("toa_thuoc.txt", 5)) {
            try {
                DonThuoc prescription = new DonThuoc(row.get(0), row.get(1), row.get(2), row.get(3),
                        LocalDate.parse(row.get(4)));
                prescriptions.put(prescription.getPrescriptionId(), prescription);
            } catch (RuntimeException exception) {
                throw invalidData("toa_thuoc.txt", exception);
            }
        }
        for (List<String> row : readRows("toa_thuoc_chi_tiet.txt", 2)) {
            DonThuoc prescription = prescriptions.get(row.get(0));
            if (prescription == null) {
                throw new IOException("Toa thuốc không tồn tại trong toa_thuoc.txt: " + row.get(0));
            }
            prescription.addMedicine(row.get(1));
        }
        return new ArrayList<>(prescriptions.values());
    }

    private List<PhieuNhap> loadImportNotes() throws IOException {
        Map<String, PhieuNhap> importNotes = new LinkedHashMap<>();
        for (List<String> row : readRows("phieu_nhap.txt", 5)) {
            try {
                PhieuNhap note = new PhieuNhap(row.get(0), row.get(1), row.get(2),
                        Double.parseDouble(row.get(4)));
                note.setImportDate(LocalDate.parse(row.get(3)));
                importNotes.put(note.getImportNoteId(), note);
            } catch (RuntimeException exception) {
                throw invalidData("phieu_nhap.txt", exception);
            }
        }
        for (List<String> row : readRows("phieu_nhap_chi_tiet.txt", 2)) {
            PhieuNhap note = importNotes.get(row.get(0));
            if (note == null) {
                throw new IOException("Phiếu nhập không tồn tại trong phieu_nhap.txt: " + row.get(0));
            }
            note.addMedicine(row.get(1));
        }
        return new ArrayList<>(importNotes.values());
    }

    private void loadInvoices() throws IOException {
        Map<String, PhieuXuat> invoices = new LinkedHashMap<>();
        for (List<String> row : readRows("hoa_don.txt", 7)) {
            try {
                PhieuXuat invoice = new PhieuXuat(row.get(0), row.get(1), row.get(2),
                        Double.parseDouble(row.get(4)), row.get(5), parseBoolean(row.get(6)));
                invoice.setCreatedAt(LocalDateTime.parse(row.get(3)));
                invoices.put(invoice.getInvoiceId(), invoice);
            } catch (RuntimeException exception) {
                throw invalidData("hoa_don.txt", exception);
            }
        }
        List<ChiTietPhieuXuat> details = new ArrayList<>();
        for (List<String> row : readRows("hoa_don_chi_tiet.txt", 5)) {
            try {
                if (!invoices.containsKey(row.get(1))) {
                    throw new IOException("Hóa đơn không tồn tại trong hoa_don.txt: " + row.get(1));
                }
                details.add(new ChiTietPhieuXuat(row.get(0), row.get(1), row.get(2),
                        Integer.parseInt(row.get(3)), Double.parseDouble(row.get(4))));
            } catch (NumberFormatException exception) {
                throw invalidData("hoa_don_chi_tiet.txt", exception);
            }
        }
        quanLyBanHang.replaceAllInvoices(new ArrayList<>(invoices.values()), details);
    }

    private List<List<String>> categoryRows() {
        List<List<String>> rows = new ArrayList<>();
        for (LoaiThuoc category : quanLyLoaiThuoc.getAllCategories()) {
            rows.add(Arrays.asList(category.getCategoryId(), category.getName(), category.getDescription()));
        }
        return rows;
    }

    private List<List<String>> customerRows() {
        List<List<String>> rows = new ArrayList<>();
        for (KhachHang customer : quanLyKhachHang.getAllCustomers()) {
            rows.add(Arrays.asList(customer.getCustomerId(), customer.getName(), customer.getPhone(),
                    Integer.toString(customer.getLoyaltyPoints()), customer.getMemberTier()));
        }
        return rows;
    }

    private List<List<String>> employeeRows() {
        List<List<String>> rows = new ArrayList<>();
        for (NhanVienKho employee : quanLyNhanVien.getAllEmployees()) {
            rows.add(Arrays.asList(employee.getEmployeeId(), employee.getName(), employee.getRole(),
                    employee.getPhone(), Double.toString(employee.getBaseSalary()),
                    Double.toString(employee.getCommission())));
        }
        return rows;
    }

    private List<List<String>> supplierRows() {
        List<List<String>> rows = new ArrayList<>();
        for (NhaCungCap supplier : quanLyNhaCungCap.getAllSuppliers()) {
            rows.add(Arrays.asList(supplier.getSupplierId(), supplier.getName(), supplier.getPhone(),
                    supplier.getAddress()));
        }
        return rows;
    }

    private List<List<String>> prescriptionRows() {
        List<List<String>> rows = new ArrayList<>();
        for (DonThuoc prescription : quanLyDonThuoc.getAllPrescriptions()) {
            rows.add(Arrays.asList(prescription.getPrescriptionId(), prescription.getCustomerId(),
                    prescription.getPatientName(), prescription.getDoctorName(),
                    prescription.getIssueDate().toString()));
        }
        return rows;
    }

    private List<List<String>> prescriptionMedicineRows() {
        List<List<String>> rows = new ArrayList<>();
        for (DonThuoc prescription : quanLyDonThuoc.getAllPrescriptions()) {
            for (String medicineId : prescription.getMedicineIds()) {
                rows.add(Arrays.asList(prescription.getPrescriptionId(), medicineId));
            }
        }
        return rows;
    }

    private List<List<String>> importRows() {
        List<List<String>> rows = new ArrayList<>();
        for (PhieuNhap note : quanLyNhapKho.getAllImportNotes()) {
            rows.add(Arrays.asList(note.getImportNoteId(), note.getSupplierId(), note.getEmployeeId(),
                    note.getImportDate().toString(), Double.toString(note.getTotalAmount())));
        }
        return rows;
    }

    private List<List<String>> importMedicineRows() {
        List<List<String>> rows = new ArrayList<>();
        for (PhieuNhap note : quanLyNhapKho.getAllImportNotes()) {
            for (String medicineId : note.getMedicineIds()) {
                rows.add(Arrays.asList(note.getImportNoteId(), medicineId));
            }
        }
        return rows;
    }

    private List<List<String>> invoiceRows() {
        List<List<String>> rows = new ArrayList<>();
        for (PhieuXuat invoice : quanLyBanHang.getAllInvoices()) {
            rows.add(Arrays.asList(invoice.getInvoiceId(), invoice.getCustomerId(), invoice.getEmployeeId(),
                    invoice.getCreatedAt().toString(), Double.toString(invoice.getTotalAmount()),
                    invoice.getPaymentMethod(), Boolean.toString(invoice.isPaid())));
        }
        return rows;
    }

    private List<List<String>> invoiceDetailRows() {
        List<List<String>> rows = new ArrayList<>();
        for (ChiTietPhieuXuat detail : quanLyBanHang.getAllDetails()) {
            rows.add(Arrays.asList(detail.getDetailId(), detail.getInvoiceId(), detail.getMedicineId(),
                    Integer.toString(detail.getQuantity()), Double.toString(detail.getUnitPrice())));
        }
        return rows;
    }

    private List<List<String>> readRows(String fileName, int expectedColumns) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        List<List<String>> rows = new ArrayList<>();
        if (!Files.exists(file)) {
            return rows;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                List<String> fields;
                try {
                    fields = parseLine(line);
                } catch (IllegalArgumentException exception) {
                    throw new IOException("Định dạng không hợp lệ tại " + file + ":" + lineNumber, exception);
                }
                if (fields.size() != expectedColumns) {
                    throw new IOException("Cần " + expectedColumns + " cột tại " + file + ":" + lineNumber);
                }
                rows.add(fields);
            }
        }
        return rows;
    }

    private void writeRows(String fileName, String header, List<List<String>> rows) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("# " + header);
            writer.newLine();
            for (List<String> row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    if (i > 0) {
                        writer.write('\t');
                    }
                    writer.write(escape(row.get(i)));
                }
                writer.newLine();
            }
        }
    }

    private static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (escaped) {
                switch (current) {
                    case 't': field.append('\t'); break;
                    case 'n': field.append('\n'); break;
                    case 'r': field.append('\r'); break;
                    case '\\': field.append('\\'); break;
                    default: throw new IllegalArgumentException("Escape không hợp lệ");
                }
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '\t') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        if (escaped) {
            throw new IllegalArgumentException("Escape chưa hoàn tất");
        }
        fields.add(field.toString());
        return fields;
    }

    private static String escape(String value) {
        String normalized = value == null ? "" : value;
        return normalized.replace("\\", "\\\\").replace("\t", "\\t")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Giá trị boolean không hợp lệ");
    }

    private IOException invalidData(String fileName, RuntimeException cause) {
        return new IOException("Dữ liệu không hợp lệ trong " + dataDirectory.resolve(fileName), cause);
    }
}
