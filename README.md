# Quản lý cửa hàng bán thuốc

Dự án Java mô phỏng quản lý cửa hàng thuốc theo hướng đối tượng (OOP). Mã nguồn được chia thành các package theo nhóm chức năng:

- `src/QuanLyChungTu`: phiếu nhập, phiếu xuất, toa thuốc, bán hàng và báo cáo.
- `src/QuanLyDoiTac`: tài khoản, nhân viên, khách hàng và nhà cung cấp.
- `src/QuanLyKhoBai`: xử lý nhập kho và cập nhật tồn thuốc.
- `src/QuanLyPhanLoai`: loại thuốc.
- `src/QuanLySanPham`: sản phẩm y tế trừu tượng, thuốc kê đơn/không kê đơn và danh sách thuốc.
- `src/Main.java`: điểm khởi chạy ứng dụng.
- `src/GiaoDien/GiaoDienConsole.java`: menu console chạy trực tiếp trong Terminal của VS Code.
- `src/KiemThuHeThong.java`: kiểm thử tự động.

Tên lớp đã được chuyển sang tiếng Việt; tên phương thức và thuộc tính được giữ nguyên để hạn chế thay đổi hành vi.
Khách hàng và nhân viên kho kế thừa từ `Nguoi`; phiếu nhập/xuất triển khai `IThanhToan`.
Danh sách thuốc được lưu ở `data/thuoc.csv` và thao tác nghiệp vụ được ghi nối tiếp vào `log.txt`.
Đây là giao diện văn bản chạy trong Terminal tích hợp của VS Code, không phải trang web và không cần trình duyệt.

## Biên dịch và chạy

Mở Terminal PowerShell trong VS Code tại thư mục dự án, sau đó chạy:

```powershell
chcp 65001
$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $sources
java -Dfile.encoding=UTF-8 -cp out KiemThuHeThong
java -Dfile.encoding=UTF-8 -cp out Main
```

`Main` mở menu console; `KiemThuHeThong` chạy các kiểm thử chức năng.
Để hiển thị tiếng Việt đúng trong PowerShell, chạy `chcp 65001` trước khi chạy chương trình.

## Các chức năng trong menu

- Thuốc: xem danh sách, thêm/sửa/xóa và lọc thuốc sắp hết hạn.
- Loại thuốc: xem, thêm, sửa và xóa loại (không cho xóa loại đang được sử dụng).
- Bán hàng: xem hóa đơn, tạo hóa đơn và tự động trừ tồn kho; thuốc kê đơn cần có toa phù hợp.
- Nhập kho: tạo phiếu nhập, xem phiếu và nhập thêm số lượng thuốc.
- Đối tác: xem, thêm, sửa và xóa khách hàng, nhân viên, nhà cung cấp; dữ liệu đang được chứng từ sử dụng sẽ được bảo vệ khỏi xóa.
- Toa thuốc: xem toa, tạo toa và kiểm tra thuốc có trong toa.
- Báo cáo: xem doanh thu ngày/tháng và thuốc sắp hết hạn.

Mỗi mục có menu con riêng; chọn `0` để quay lại. Danh sách thuốc được nạp khi khởi động và lưu sau khi thêm, sửa, xóa, bán hoặc nhập kho; các dữ liệu khác hiện chỉ lưu trong bộ nhớ.
