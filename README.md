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
Danh sách thuốc được lưu ở `data/thuoc.txt`; loại thuốc, khách hàng, nhân viên, nhà cung cấp, toa thuốc, hóa đơn và phiếu nhập được lưu thành các file văn bản trong `data`. Thao tác nghiệp vụ được ghi nối tiếp vào `log.txt`.
Các danh mục thuốc, loại thuốc, khách hàng, nhân viên và nhà cung cấp hỗ trợ thêm, sửa, xóa và tìm kiếm trong menu console. Mỗi thay đổi thành công được lưu ngay vào các file trong `data`, không cần thoát về menu chính.
Khi sửa thông tin, nhấn Enter ở trường muốn giữ nguyên giá trị hiện tại; chỉ nhập giá trị mới cho những trường cần thay đổi.
Số điện thoại khách hàng, nhân viên và nhà cung cấp phải có đúng 10 chữ số; mã của từng đối tượng không được trùng. Báo cáo doanh thu tháng yêu cầu nhập năm rồi tháng (1–12); báo cáo hạn dùng yêu cầu ngày theo định dạng `dd-MM-yyyy`.
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
Ứng dụng tự nạp dữ liệu từ các file trong `data` khi khởi động và lưu sau mỗi chức năng hoặc khi thoát. Các file `.txt` dùng UTF-8, phân tách cột bằng tab; không xóa hoặc sửa thủ công các file khi ứng dụng đang chạy.
Khi nhập ngày trong giao diện, dùng định dạng `dd-MM-yyyy`, ví dụ `10-10-2030`. File dữ liệu vẫn lưu ngày theo định dạng ISO `yyyy-MM-dd` để tương thích với dữ liệu hiện có.
Để hiển thị tiếng Việt đúng trong PowerShell, chạy `chcp 65001` trước khi chạy chương trình.

## Các chức năng trong menu

- Thuốc: xem danh sách, thêm/sửa/xóa và lọc thuốc sắp hết hạn.
- Loại thuốc: xem, thêm, sửa và xóa loại (không cho xóa loại đang được sử dụng).
- Bán hàng: xem hóa đơn, tạo hóa đơn và tự động trừ tồn kho; thuốc kê đơn cần có toa phù hợp.
- Nhập kho: tạo phiếu nhập, xem phiếu và nhập thêm số lượng thuốc.
- Đối tác: xem, thêm, sửa và xóa khách hàng, nhân viên, nhà cung cấp; dữ liệu đang được chứng từ sử dụng sẽ được bảo vệ khỏi xóa.
- Toa thuốc: xem toa, tạo toa và kiểm tra thuốc có trong toa.
- Báo cáo: xem doanh thu ngày/tháng và thuốc sắp hết hạn.

Mỗi mục có menu con riêng; chọn `0` để quay lại. Danh sách thuốc được nạp khi khởi động và lưu sau khi thêm, sửa, xóa, bán hoặc nhập kho. Các dữ liệu còn lại được ghi vào file khi quay lại menu chính hoặc thoát chương trình.
# OOP
