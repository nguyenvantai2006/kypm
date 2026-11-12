# Đặc tả Use Case: UC-04 - Quản lý Khách hàng (Customer Management)

## 1. Thông tin chung

* **Mã Use Case:** UC-04

* **Tên Use Case:** Quản lý Khách hàng

* **Tác nhân (Actor):** Nhân viên cửa hàng (Shop staff) được cấp quyền.

* **Mô tả ngắn gọn:** Tác nhân sử dụng chức năng này để tra cứu, thêm mới, và cập nhật thông tin khách hàng. Hệ thống đồng thời theo dõi lịch sử mua hàng và tổng giá trị chi tiêu của từng khách hàng để phục vụ cho các báo cáo khách hàng thân thiết.

## 2. Tiền điều kiện (Preconditions)

* Nhân viên đang duy trì phiên làm việc `AuthSession` hợp lệ.

* Tài khoản của nhân viên có cờ phân quyền quản lý khách hàng (`QL_KHACHHANG`) thông qua cơ sở dữ liệu `QuyenDAO`.

* Giao diện `KhachHangPanel` đã được mở thông qua thanh điều hướng của `MainFrame`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Nhân viên nhấp vào menu "Khách hàng" trên `MainFrame`.

2. Hệ thống kiểm tra quyền `permissionForKey("KHACHHANG")` tương đương với `MainFrame.Permission.QL_KHACHHANG`. Nếu hợp lệ, tải `KhachHangPanel`.

3. Hệ thống truy vấn cơ sở dữ liệu (qua lớp BUS và DAO của khách hàng) để lấy danh sách khách hàng hiện có và hiển thị lên bảng dữ liệu.

4. Nhân viên thực hiện một trong các thao tác: Thêm mới, Sửa thông tin, hoặc Tìm kiếm. 
   * *(Quy trình Thêm mới)* Nhân viên điền thông tin vào form (Tên khách hàng, Số điện thoại, Địa chỉ).

5. Nhân viên nhấn nút "Lưu" (hoặc "Thêm").

6. Lớp nghiệp vụ (BUS) tiếp nhận và thực hiện kiểm tra tính hợp lệ (Validation) của các trường dữ liệu (không được rỗng, định dạng số điện thoại hợp lệ).

7. Hệ thống gửi lệnh `INSERT`/`UPDATE` xuống DAO để lưu thông tin vào cơ sở dữ liệu SQL Server qua cầu nối `DBConnect`.

8. Hệ thống ghi nhận thành công, gọi phương thức `refreshData()` (implement từ `IRefreshable`) để làm mới lại danh sách trên giao diện.

9. Song song, khi khách hàng này phát sinh giao dịch mua bán, hệ thống tự động cập nhật và đóng gói dữ liệu vào `TopKhachHangDTO` bao gồm các trường: `maKH`, `tenKH`, tổng số lượng hóa đơn (`soHoaDon`), và tổng chi tiêu dạng số thập phân có độ chính xác cao (`tongChiTieu` bằng `BigDecimal`). Use Case kết thúc.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Thiếu thông tin bắt buộc (Missing Required Fields)**

  * *Tại bước 6:* Nhân viên bỏ trống các trường bắt buộc như Tên khách hàng hoặc Số điện thoại.

  * *Hệ thống:* Lớp BUS từ chối xử lý, hiển thị cảnh báo "Vui lòng nhập đầy đủ thông tin bắt buộc" và tô đỏ các ô nhập liệu bị thiếu.

* **E2: Dữ liệu trùng lặp (Duplicate Data)**

  * *Tại bước 6 hoặc 7:* Nhân viên nhập số điện thoại đã tồn tại trong hệ thống.

  * *Hệ thống:* DAO hoặc BUS ném ra lỗi trùng lặp, giao diện hiển thị thông báo "Số điện thoại này đã được đăng ký cho một khách hàng khác". Khuyên người dùng sử dụng chức năng tìm kiếm thay vì thêm mới.

* **E3: Sai định dạng dữ liệu (Invalid Format)**

  * *Tại bước 6:* Số điện thoại chứa ký tự chữ cái, hoặc quá ngắn/quá dài.

  * *Hệ thống:* Lớp BUS chặn lại, hiển thị "Định dạng số điện thoại không hợp lệ".

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Thông tin khách hàng được lưu vào cơ sở dữ liệu. Dữ liệu này sẵn sàng để liên kết với các Hóa đơn trong phân hệ Bán hàng (UC-03) và có thể được truy xuất bởi `ThongKeDAO` để tìm ra Top khách hàng chi tiêu nhiều nhất.

* **Thất bại:** Trạng thái CSDL không thay đổi, dữ liệu rác không bị lọt vào hệ thống.