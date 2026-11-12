# Đặc tả Use Case: UC-07 - Quản lý Danh mục Sản phẩm (Catalog Management)

## 1. Thông tin chung

* **Mã Use Case:** UC-07

* **Tên Use Case:** Quản lý Danh mục Sản phẩm

* **Tác nhân (Actor):** Nhân viên cửa hàng hoặc Quản lý (được phân quyền).

* **Mô tả ngắn gọn:** Use case này cho phép người dùng thêm mới, cập nhật, xóa (hoặc ẩn), và tra cứu thông tin các sản phẩm giày trong cửa hàng. Phân hệ này là cốt lõi để cung cấp dữ liệu nền tảng cho việc bán hàng và nhập kho.

## 2. Tiền điều kiện (Preconditions)

* Tác nhân đang đăng nhập hợp lệ với đối tượng `AuthSession`.

* Tác nhân được cấp quyền quản lý sản phẩm (cờ phân quyền tương ứng trong `QuyenDTO` được kích hoạt).

* Giao diện `SanPhamPanel` đã được tải và hiển thị thành công thông qua thanh điều hướng của `MainFrame`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Nhân viên nhấp vào menu "Sản phẩm" trên giao diện điều hướng `MainFrame`.

2. Hệ thống tải `SanPhamPanel`, đồng thời gọi `SanPhamBUS` để yêu cầu danh sách sản phẩm.

3. `SanPhamBUS` truy vấn cơ sở dữ liệu thông qua `SanPhamDAO` và kết nối `DBConnect`, sau đó trả về danh sách để hiển thị lên bảng dữ liệu.

4. Nhân viên thực hiện thao tác Thêm mới (hoặc Cập nhật) sản phẩm:

   * Nhân viên nhập/sửa các thông tin chi tiết: Mã sản phẩm (SKU), Tên sản phẩm, Loại, Kích cỡ (Size), Màu sắc, Số lượng tồn kho, và Giá bán.

   * *Lưu ý:* Trường Giá bán phải được hệ thống xử lý bằng kiểu dữ liệu `BigDecimal` để đảm bảo độ chính xác.

5. Nhân viên nhấn nút "Lưu".

6. `SanPhamBUS` tiếp nhận dữ liệu và thực hiện kiểm tra tính hợp lệ (Validation):

   * Tên sản phẩm, Mã sản phẩm không được để trống.

   * Giá bán phải lớn hơn 0.

   * Số lượng tồn kho không được là số âm.

7. Nếu dữ liệu hợp lệ, hệ thống chuyển lệnh xuống `SanPhamDAO` để thực hiện câu lệnh `INSERT` hoặc `UPDATE` vào cơ sở dữ liệu SQL Server.

8. Sau khi lưu thành công, hệ thống gọi phương thức `refreshData()` (thừa kế từ interface `IRefreshable`) để tự động cập nhật lại danh sách sản phẩm trên lưới dữ liệu mà không cần khởi động lại màn hình. Use Case kết thúc.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Thiếu trường dữ liệu bắt buộc (Missing Required Fields)**

  * *Tại bước 6:* Nhân viên bỏ trống Tên sản phẩm hoặc Mã sản phẩm.

  * *Hệ thống:* Lớp nghiệp vụ (BUS) từ chối thao tác, giao diện hiển thị cảnh báo "Vui lòng nhập đầy đủ các trường thông tin bắt buộc (được đánh dấu *)".

* **E2: Mã sản phẩm bị trùng lặp (Duplicate SKU)**

  * *Tại bước 6 hoặc 7:* Mã sản phẩm (SKU) nhập vào đã tồn tại trong CSDL.

  * *Hệ thống:* Lớp DAO/BUS ném ra ngoại lệ. Giao diện bắt lỗi và hiển thị thông báo "Mã sản phẩm này đã tồn tại. Vui lòng nhập mã khác".

* **E3: Dữ liệu số không hợp lệ (Invalid Numeric Values)**

  * *Tại bước 6:* Nhân viên nhập giá bán hoặc số lượng tồn kho là số âm, hoặc nhập chữ vào trường bắt buộc là số.

  * *Hệ thống:* `SanPhamBUS` chặn luồng xử lý, hiển thị thông báo "Giá bán và Số lượng tồn kho phải là số và lớn hơn hoặc bằng 0".

* **E4: Xóa sản phẩm đang có giao dịch (Delete Product in Use)**

  * *Luồng xóa:* Nhân viên chọn xóa một sản phẩm đã từng được bán (đã nằm trong chi tiết hóa đơn).

  * *Hệ thống:* DAO chặn lệnh `DELETE` do dính khóa ngoại (Foreign Key) từ bảng Hóa đơn. Hệ thống từ chối xóa vĩnh viễn và tự động chuyển trạng thái sản phẩm thành "Ngừng kinh doanh" (Inactive).

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Cơ sở dữ liệu danh mục sản phẩm được cập nhật. Dữ liệu này ngay lập tức khả dụng cho màn hình bán hàng (`BanHangPanel`), nhập hàng (`NhapHangPanel`), và thống kê (`ThongKePanel`).

* **Thất bại:** Cơ sở dữ liệu không có sự thay đổi. Tránh được rủi ro rác dữ liệu hoặc sai lệch tài chính.