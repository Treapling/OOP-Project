import GiaoDien.GiaoDienConsole;
import QuanLyChungTu.QuanLyBanHang;
import QuanLyChungTu.QuanLyDonThuoc;
import QuanLyChungTu.DataDirectory;
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
    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(
                new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));

        Path dataDirectory = DataDirectory.resolve();
        System.setProperty("pharmacy.data.dir", dataDirectory.toString());
        QuanLyLoaiThuoc quanLyLoaiThuoc = new QuanLyLoaiThuoc();
        DanhSachThuoc danhSachThuoc = new DanhSachThuoc();
        loadOrCreate(dataDirectory.resolve("thuoc.txt"), danhSachThuoc::docFile, danhSachThuoc::ghiFile);
        Path batchesFile = dataDirectory.resolve("lo-thuoc.txt");
        if (Files.exists(batchesFile)) {
            danhSachThuoc.docBatchFile(batchesFile.toString());
        } else {
            danhSachThuoc.taoLoBanDau(batchesFile.toString());
        }
        loadOrCreate(dataDirectory.resolve("loai-thuoc.txt"),
                quanLyLoaiThuoc::docFile, quanLyLoaiThuoc::ghiFile);
        QuanLyNhanVienKho quanLyNhanVien = new QuanLyNhanVienKho();
        loadOrCreate(dataDirectory.resolve("nhan-vien.txt"),
                quanLyNhanVien::docFile, quanLyNhanVien::ghiFile);
        QuanLyKhachHang quanLyKhachHang = new QuanLyKhachHang();
        loadOrCreate(dataDirectory.resolve("khach-hang.txt"),
                quanLyKhachHang::docFile, quanLyKhachHang::ghiFile);
        QuanLyNhaCungCap quanLyNhaCungCap = new QuanLyNhaCungCap();
        loadOrCreate(dataDirectory.resolve("nha-cung-cap.txt"),
                quanLyNhaCungCap::docFile, quanLyNhaCungCap::ghiFile);
        QuanLyBanHang quanLyBanHang = new QuanLyBanHang(danhSachThuoc);
        loadOrCreate(dataDirectory.resolve("hoa-don.txt"),
                quanLyBanHang::docFile, quanLyBanHang::ghiFile);
        QuanLyNhapKho quanLyNhapKho = new QuanLyNhapKho(danhSachThuoc);
        loadOrCreate(dataDirectory.resolve("phieu-nhap.txt"),
                quanLyNhapKho::docFile, quanLyNhapKho::ghiFile);
        QuanLyDonThuoc quanLyDonThuoc = new QuanLyDonThuoc();
        loadOrCreate(dataDirectory.resolve("toa-thuoc.txt"),
                quanLyDonThuoc::docFile, quanLyDonThuoc::ghiFile);

        new GiaoDienConsole(quanLyLoaiThuoc, danhSachThuoc, quanLyNhanVien,
                quanLyKhachHang, quanLyNhaCungCap, quanLyBanHang,
                quanLyNhapKho, quanLyDonThuoc, dataDirectory).run();
    }

    private static void loadOrCreate(Path path, FileReader reader, FileWriter writer) throws IOException {
        if (Files.exists(path)) {
            reader.read(path.toString());
        } else {
            writer.write(path.toString());
        }
    }

    @FunctionalInterface
    private interface FileReader {
        void read(String path) throws IOException;
    }

    @FunctionalInterface
    private interface FileWriter {
        void write(String path) throws IOException;
    }
}
