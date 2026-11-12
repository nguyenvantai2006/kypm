# Đặc tả Use Case: UC-06 - Quản lý Bảo hành (Warranty Management)

## 1. Thông tin chung

* **Mã Use Case:** UC-06

* **Tên Use Case:** Quản lý Bảo hành

* **Tác nhân (Actor):** Nhân viên cửa hàng (Shop staff).

* **Mô tả ngắn gọn:** Use case này mô tả quy trình nhân viên tiếp nhận sản phẩm lỗi từ khách hàng, tra cứu thông tin hóa đơn để xác minh thời hạn bảo hành, lập phiếu tiếp nhận bảo hành và theo dõi trạng thái xử lý sự cố.

## 2. Tiền điều kiện (Preconditions)

* Nhân viên đang duy trì phiên làm việc `AuthSession` hợp lệ.

* Tài khoản của nhân viên có cờ phân quyền truy cập chức năng Bảo hành.

* Cửa sổ điều hướng chính `MainFrame` đã chuyển hướng và tải thành công giao diện `BaoHanhPanel`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Khách hàng mang sản phẩm lỗi đến cửa hàng. Nhân viên mở giao diện `BaoHanhPanel`.

2. Nhân viên nhập mã hóa đơn hoặc số điện thoại của khách hàng vào ô tìm kiếm để tra cứu thông tin giao dịch.

3. Hệ thống truy vấn cơ sở dữ liệu qua các lớp DAO, trả về danh sách các sản phẩm khách đã mua kèm theo thông tin ngày mua và số tháng bảo hành quy định.

4. Nhân viên chọn sản phẩm gặp sự cố. Lớp nghiệp vụ (BUS) tính toán và đối chiếu ngày mua với ngày hiện tại để xác nhận sản phẩm vẫn còn trong thời hạn bảo hành.

5. Hệ thống hiển thị trạng thái "Hợp lệ". Nhân viên điền thông tin mô tả tình trạng lỗi của sản phẩm và hướng xử lý dự kiến (gửi hãng, sửa chữa tại chỗ).

6. Nhân viên nhấn nút "Tạo phiếu bảo hành".

7. Hệ thống thực hiện lưu thông tin Phiếu bảo hành vào cơ sở dữ liệu SQL Server thông qua lớp DAO tương ứng.

8. Hệ thống hiển thị thông báo "Tạo phiếu bảo hành thành công" và gọi phương thức `refreshData()` (từ interface `IRefreshable`) để làm sạch form nhập liệu, cập nhật lại danh sách các phiếu bảo hành đang xử lý.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Không tìm thấy thông tin mua hàng (Purchase Record Not Found)**

  * *Tại bước 3:* Số điện thoại hoặc mã hóa đơn không tồn tại trong hệ thống.

  * *Hệ thống:* Hiển thị thông báo "Không tìm thấy dữ liệu mua hàng. Vui lòng kiểm tra lại thông tin cung cấp".

* **E2: Sản phẩm đã hết hạn bảo hành (Warranty Expired)**

  * *Tại bước 4:* Lớp BUS tính toán và phát hiện khoảng thời gian từ ngày mua đến hiện tại đã vượt quá số tháng bảo hành quy định.

  * *Hệ thống:* Giao diện hiển thị cảnh báo "Sản phẩm đã hết hạn bảo hành". Hệ thống có thể chặn tạo phiếu hoặc chuyển sang luồng "Sửa chữa có tính phí" (tùy thuộc vào quy trình nghiệp vụ bổ sung).

* **E3: Bỏ trống mô tả tình trạng lỗi (Missing Defect Description)**

  * *Tại bước 6:* Nhân viên không nhập tình trạng hư hỏng của sản phẩm.

  * *Hệ thống:* Lớp BUS chặn tiến trình lưu, hiển thị thông báo "Vui lòng nhập mô tả tình trạng lỗi của sản phẩm".

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Một Phiếu bảo hành mới được ghi nhận vào CSDL với trạng thái khởi tạo (ví dụ: "Đang tiếp nhận" hoặc "Đang xử lý"). Khách hàng và sản phẩm được liên kết chặt chẽ với phiếu này.

* **Thất bại:** Không có dữ liệu bảo hành nào được tạo ra.