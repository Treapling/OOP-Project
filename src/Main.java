import GiaoDien.GiaoDienConsole;
import QuanLyChungTu.QuanLyBanHang;
import QuanLyChungTu.QuanLyDonThuoc;
import QuanLyDoiTac.QuanLyKhachHang;
import QuanLyDoiTac.QuanLyNhaCungCap;
import QuanLyDoiTac.QuanLyNhanVienKho;
import QuanLyKhoBai.QuanLyNhapKho;
import QuanLyPhanLoai.QuanLyLoaiThuoc;
import QuanLySanPham.DanhSachThuoc;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final Path MEDICINES_FILE = Path.of("data", "thuoc.csv");

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(
                new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));

        QuanLyLoaiThuoc quanLyLoaiThuoc = new QuanLyLoaiThuoc();
        DanhSachThuoc danhSachThuoc = new DanhSachThuoc();
        if (Files.exists(MEDICINES_FILE)) {
            danhSachThuoc.docFile(MEDICINES_FILE.toString());
        } else {
            danhSachThuoc.ghiFile(MEDICINES_FILE.toString());
        }
        QuanLyNhanVienKho quanLyNhanVien = new QuanLyNhanVienKho();
        QuanLyKhachHang quanLyKhachHang = new QuanLyKhachHang();
        QuanLyNhaCungCap quanLyNhaCungCap = new QuanLyNhaCungCap();
        QuanLyBanHang quanLyBanHang = new QuanLyBanHang(danhSachThuoc);
        QuanLyNhapKho quanLyNhapKho = new QuanLyNhapKho(danhSachThuoc);
        QuanLyDonThuoc quanLyDonThuoc = new QuanLyDonThuoc();

        new GiaoDienConsole(quanLyLoaiThuoc, danhSachThuoc, quanLyNhanVien,
                quanLyKhachHang, quanLyNhaCungCap, quanLyBanHang,
                quanLyNhapKho, quanLyDonThuoc).run();
    }
}
