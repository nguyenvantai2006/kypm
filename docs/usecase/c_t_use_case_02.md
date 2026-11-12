# Đặc tả Use Case: UC-02 - Điều hướng và Kiểm soát quyền truy cập (Main Navigation)

## 1. Thông tin chung

* **Mã Use Case:** UC-02
* **Tên Use Case:** Điều hướng và Kiểm soát quyền truy cập
* **Tác nhân (Actor):** Nhân viên cửa hàng (Shop staff) đã đăng nhập thành công.
* **Mô tả ngắn gọn:** Use case này mô tả quá trình màn hình chính (`MainFrame`) đọc dữ liệu phiên làm việc để thiết lập các menu chức năng dựa trên quyền hạn của nhân viên, và xử lý điều hướng khi nhân viên chọn một chức năng nghiệp vụ bất kỳ.

## 2. Tiền điều kiện (Preconditions)

* Use Case UC-01 (Đăng nhập) đã thực thi thành công.
* Đối tượng phiên làm việc `AuthSession` đang tồn tại, bên trong chứa thông tin tài khoản (`NhanVienDTO`) và ma trận quyền hạn (`QuyenDTO`) hợp lệ.
* Cửa sổ `MainFrame` đã được khởi tạo và hiển thị lên màn hình.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Ngay khi `MainFrame` mở ra, nó tiếp nhận dữ liệu phân quyền (`supplies role`) từ đối tượng `AuthSession`.
2. Hệ thống duyệt qua danh sách các menu chức năng có sẵn trên giao diện.
3. Với mỗi chức năng, `MainFrame` gọi logic kiểm tra quyền (Ví dụ: thông qua phương thức mapping như `permissionForKey`).
   * *Ví dụ:* Để hiển thị menu Khách hàng hoặc Voucher, hệ thống kiểm tra khóa `"KHACHHANG"` hoặc `"VOUCHER"` để map với quyền `QL_KHACHHANG`.
   * *Ví dụ:* Để hiển thị menu Bán hàng hoặc Hóa đơn, hệ thống map khóa `"BANHANG"` với quyền `QL_BANHANG`.
4. Hệ thống đối chiếu với đối tượng `QuyenDTO`. Nếu cờ quyền hạn tương ứng được bật (ví dụ giá trị bằng `1`), nút menu đó sẽ được hiển thị và cho phép click.
5. Nhân viên giao tiếp với ứng dụng bằng cách nhấp vào một menu chức năng hợp lệ trên thanh điều hướng.
6. `MainFrame` xử lý sự kiện click và điều hướng (`navigates`) để hiển thị Panel nghiệp vụ tương ứng vào vùng hiển thị trung tâm. Các đích đến bao gồm: `BanHangPanel`, `SanPhamPanel`, `NhapHangPanel`, `KhachHangPanel`, `DoiTraPanel`, `BaoHanhPanel`, hoặc `ThongKePanel`.
7. Panel mới tải thành công (có thể gọi hàm `refreshData()` nếu có implement `IRefreshable` để làm mới dữ liệu). Use Case kết thúc, chuyển quyền điều khiển sang các Use Case nghiệp vụ (như UC-03 Bán hàng, UC-07 Sản phẩm).

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Không có quyền truy cập chức năng (Permission Denied)**
  * *Tại bước 4:* Nếu cờ quyền trong `QuyenDTO` có giá trị là `0` (không được cấp quyền).
  * *Hệ thống:* `MainFrame` sẽ ẩn (hide) hoàn toàn nút menu đó, hoặc làm xám (disable) không cho phép click.

* **E2: Cố tình truy cập qua đường vòng (Bypass Attempt)**
  * *Tại bước 6:* Nếu có một luồng sự kiện ép hệ thống load một Panel (ví dụ: `ThongKePanel`) mà nhân viên không có cờ quyền `QL_THONGKE`.
  * *Hệ thống:* Bắt lỗi ở cấp độ Panel/Frame, chặn load giao diện, hiển thị thông báo "Bạn không có quyền truy cập chức năng này" và giữ nguyên màn hình hiện tại.

* **E3: Mất phiên làm việc bất ngờ (Session Expired / Null)**
  * *Tại bước 1:* Nếu đối tượng `AuthSession` bị null hoặc mất dữ liệu `QuyenDTO` (ví dụ do lỗi bộ nhớ).
  * *Hệ thống:* `MainFrame` sẽ bắt lỗi, hiển thị thông báo "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại". Sau đó, đóng `MainFrame` và mở lại `LoginFrame`.

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Màn hình làm việc chính chỉ hiển thị các chức năng nhân viên được phép dùng. Giao diện Panel đích được load lên thành công.
* **Thất bại:** Hệ thống chặn truy cập trái phép hoặc ép đăng nhập lại để đảm bảo tính bảo mật.