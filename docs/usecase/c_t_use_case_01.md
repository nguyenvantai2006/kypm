# Đặc tả Use Case: UC-01 - Đăng nhập hệ thống (Login)

## 1. Thông tin chung
* **Mã Use Case:** UC-01
* **Tên Use Case:** Đăng nhập hệ thống
* **Tác nhân (Actor):** Nhân viên cửa hàng (Shop staff)
* **Mô tả ngắn gọn:** Tác nhân nhập thông tin xác thực để truy cập vào hệ thống phần mềm quản lý cửa hàng giày. Quá trình này sẽ xác minh danh tính và tải các quyền hạn tương ứng của người dùng.

## 2. Tiền điều kiện (Preconditions)
* Hệ thống ứng dụng (`App.java`) đã được khởi động thành công và mở kết nối JDBC (`DBConnect.java`) đến cơ sở dữ liệu SQL Server.
* Tác nhân đã được cấp một tài khoản (Tên đăng nhập và Mật khẩu) hợp lệ trong hệ thống.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)
1. Hệ thống hiển thị màn hình đăng nhập `LoginFrame` khi ứng dụng khởi động.
2. Nhân viên nhập thông tin Tên đăng nhập (Username) và Mật khẩu (Password), sau đó nhấn nút "Đăng nhập".
3. `LoginFrame` tiếp nhận dữ liệu và gửi yêu cầu xác thực (`requests login`) đến lớp nghiệp vụ `AuthBUS`.
4. `AuthBUS` thực hiện gọi `NhanVienDAO` để tra cứu tài khoản (`looks up account`) trong cơ sở dữ liệu. Hệ thống xác nhận thông tin đăng nhập hoàn toàn trùng khớp.
5. Sau khi xác thực danh tính thành công, `AuthBUS` tiếp tục gọi `QuyenDAO` để tải ma trận quyền (`loads role`) tương ứng với chức vụ của nhân viên này.
6. `AuthBUS` khởi tạo thành công đối tượng `AuthSession` đóng gói 2 thông tin: `NhanVienDTO` (chứa dữ liệu cá nhân của nhân viên) và `QuyenDTO` (chứa các cờ phân quyền).
7. Hệ thống cấp quyền truy cập, `LoginFrame` đóng lại và mở màn hình điều hướng chính `MainFrame` (`opens on success`).
8. `AuthSession` cung cấp dữ liệu vai trò (`supplies role`) cho `MainFrame` để thiết lập hiển thị các tính năng menu (như Bán hàng, Khách hàng) tương ứng với cờ quyền hạn. Use Case kết thúc.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Thiếu thông tin bắt buộc (Validation Error)** 
  * *Tại bước 2:* Nếu nhân viên để trống ô Tên đăng nhập hoặc Mật khẩu và nhấn "Đăng nhập".
  * *Hệ thống:* `LoginFrame` chặn trực tiếp (không gửi xuống `AuthBUS`), hiển thị thông báo lỗi "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu", và yêu cầu nhập lại. Use Case quay lại bước 2.

* **E2: Thông tin xác thực không chính xác (Authentication Failed)**
  * *Tại bước 4:* Lớp `NhanVienDAO` tra cứu không thấy tài khoản hoặc mật khẩu không khớp.
  * *Hệ thống:* `AuthBUS` trả về trạng thái thất bại. `LoginFrame` hiển thị thông báo "Tên đăng nhập hoặc mật khẩu không chính xác" và xóa trắng trường Mật khẩu. Use Case quay lại bước 2.

* **E3: Tài khoản bị khóa (Account Suspended)**
  * *Tại bước 4:* Lớp `NhanVienDAO` tìm thấy tài khoản và đúng mật khẩu, nhưng trạng thái tài khoản đang là ngưng hoạt động (inactive).
  * *Hệ thống:* `AuthBUS` từ chối đăng nhập. Giao diện hiển thị thông báo "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên". Use Case quay lại bước 1.

* **E4: Mất kết nối Cơ sở dữ liệu (Database Connection Error)**
  * *Tại bước 4 hoặc bước 5:* Lớp `DBConnect` báo lỗi không thể truy cập CSDL SQL Server.
  * *Hệ thống:* Ném ra Exception hệ thống. `LoginFrame` bắt lỗi và hiển thị cảnh báo "Lỗi kết nối cơ sở dữ liệu. Vui lòng kiểm tra lại mạng hoặc liên hệ IT". Tiến trình đăng nhập bị gián đoạn.

## 5. Hậu điều kiện (Postconditions)
* **Thành công:** Một phiên làm việc `AuthSession` hợp lệ được sinh ra và giữ trạng thái tĩnh (hoặc truyền vào `MainFrame`). Giao diện hiển thị chính xác các chức năng mà nhân viên được phép truy cập dựa trên hàm `permissionForKey`.
* **Thất bại:** Không có phiên làm việc nào được tạo ra. Người dùng vẫn ở lại màn hình `LoginFrame` hoặc thoát ứng dụng.