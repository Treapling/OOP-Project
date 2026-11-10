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
Danh mục và nghiệp vụ được lưu bằng các file văn bản UTF-8 trong `data/`: `thuoc.txt`,
`loai-thuoc.txt`, `khach-hang.txt`, `nhan-vien.txt`, `nha-cung-cap.txt`,
`hoa-don.txt`, `phieu-nhap.txt`, `toa-thuoc.txt` và `lo-thuoc.txt`. Mỗi file có 5 bản ghi mẫu
liên kết theo mã để có thể thử luồng bán hàng, nhập kho và kê toa ngay sau khi chạy.
Các manager nạp dữ liệu khi khởi động và tự ghi lại file tương ứng ngay sau mỗi
thao tác thay đổi; `log.txt` vẫn ghi nhật ký nghiệp vụ. File dùng các trường TSV
(phân cách bằng tab), escape tab/xuống dòng/backslash trong giá trị.
Thư mục dữ liệu được dò từ thư mục dự án hoặc vị trí bytecode; có thể chỉ định
đường dẫn khác bằng `-Dpharmacy.data.dir=<đường-dẫn>`. Mục 8 trong menu hiển thị
đường dẫn tuyệt đối, số bản ghi và trạng thái đọc được của từng file. Các lớp trong
`out/` chỉ là bytecode; sau khi cập nhật mã nguồn cần biên dịch lại trước khi chạy.
Đây là giao diện văn bản chạy trong Terminal tích hợp của VS Code, không phải trang web và không cần trình duyệt.

## Biên dịch và chạy

Mở Terminal PowerShell trong VS Code tại thư mục dự án, sau đó chạy:

```powershell
chcp 65001
$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $sources
java -cp out KiemThuHeThong
java -cp out Main
```

`Main` mở menu console; `KiemThuHeThong` chạy các kiểm thử chức năng.
Để hiển thị tiếng Việt đúng trong PowerShell, chạy `chcp 65001` trước khi chạy chương trình.

## Các chức năng trong menu

- Thuốc: xem danh sách, thêm/sửa/xóa và lọc thuốc sắp hết hạn.
- Kho: theo dõi lô/hạn dùng theo FEFO, cảnh báo định mức tối thiểu và tìm thuốc theo
  từ khóa gần đúng, mã loại và nhà sản xuất.
- Loại thuốc: xem, thêm, sửa và xóa loại (không cho xóa loại đang được sử dụng).
- Bán hàng: xem hóa đơn, tạo hóa đơn và tự động trừ tồn kho; thuốc kê đơn cần có toa phù hợp.
- Nhập kho: tạo phiếu nhập, xem phiếu và nhập thêm số lượng thuốc.
- Đối tác: xem, thêm, sửa và xóa khách hàng, nhân viên, nhà cung cấp; dữ liệu đang được chứng từ sử dụng sẽ được bảo vệ khỏi xóa.
- Toa thuốc: xem toa, tạo toa và kiểm tra thuốc có trong toa.
- Báo cáo: xem doanh thu ngày/tháng và thuốc sắp hết hạn.

Mỗi mục có menu con riêng; chọn `0` để quay lại. Hóa đơn và phiếu nhập lưu cả
chi tiết mặt hàng, số lượng, đơn giá tại thời điểm giao dịch và trạng thái thanh toán;
tồn kho
được cập nhật đồng thời theo nghiệp vụ bán/nhập. Hệ thống từ chối bán thuốc đã
hết hạn, bán vượt tồn hoặc xóa dữ liệu đang được chứng từ tham chiếu. Đây là mô
hình học tập dùng file cục bộ, chưa thay thế cơ sở dữ liệu giao dịch, quản lý lô,
truy xuất nguồn gốc hay kiểm soát người dùng của phần mềm nhà thuốc thực tế.

Tồn kho được lưu theo đơn vị nhỏ nhất; quy cách 1 hộp/1 vỉ/1 viên cấu hình riêng
cho từng thuốc. Nhập kho ghi số lô và hạn dùng; bán lẻ chọn đơn vị, quy đổi số
lượng về đơn vị gốc và xuất lô còn hạn gần nhất trước (FEFO). Tồn dưới định mức
chỉ đưa ra lượng đề xuất, không tự tạo phiếu nhập hay tự đặt hàng. Có thể cách ly
lô bị thu hồi; lô cách ly không được bán và số lô đã bán được ghi trên hóa đơn.
Mỗi sản phẩm
mẫu có mức định mức và quy cách đóng gói minh họa.

`TaiKhoan.checkPassword` hiện được gọi bởi `BaoMatUtil.login` và
`BaoMatUtil.changePassword`; tuy nhiên `Main`/giao diện console chưa tích hợp
đăng nhập. Vì vậy dữ liệu tài khoản không được ghi vào các file mẫu văn bản
(tránh lưu mật khẩu dạng rõ khi tính năng này chưa có lưu trữ an toàn).
