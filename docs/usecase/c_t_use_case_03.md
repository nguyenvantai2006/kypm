# Đặc tả Use Case: UC-03 - Quản lý Bán hàng tại quầy (Point of sale)

## 1. Thông tin chung

* **Mã Use Case:** UC-03

* **Tên Use Case:** Quản lý Bán hàng tại quầy

* **Tác nhân (Actor):** Nhân viên cửa hàng (Shop staff) có quyền bán hàng.

* **Mô tả ngắn gọn:** Use case này mô tả quy trình nhân viên thực hiện tạo hóa đơn, thêm sản phẩm vào giỏ, thanh toán cho khách hàng và cập nhật dữ liệu tồn kho, doanh thu vào hệ thống.

## 2. Tiền điều kiện (Preconditions)

* Nhân viên đã đăng nhập và đang sở hữu `AuthSession` hợp lệ.

* Nhân viên được phân quyền truy cập chức năng Bán hàng (cờ `QL_BANHANG` được bật trong `QuyenDTO`).

* Cửa sổ điều hướng chính `MainFrame` đã chuyển hướng thành công đến giao diện `BanHangPanel`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Nhân viên tương tác trên `BanHangPanel` để tìm kiếm hoặc quét mã sản phẩm khách hàng muốn mua.

2. Hệ thống truy vấn thông tin sản phẩm thông qua `SanPhamBUS` và `SanPhamDAO` để hiển thị tên, giá bán và số lượng tồn kho hiện tại.

3. Nhân viên nhập số lượng mua và thêm sản phẩm vào giỏ hàng tạm (Hóa đơn tạm).

4. Hệ thống tự động tính toán tổng tiền của hóa đơn.

5. Nhân viên có thể tùy chọn thêm thông tin khách hàng (chọn từ danh sách hoặc tạo mới) để tích điểm (liên kết logic với `KhachHangPanel` và cập nhật `TopKhachHangDTO`).

6. Nhân viên nhận tiền từ khách, nhập số tiền khách đưa và hệ thống tính số tiền thối lại.

7. Nhân viên nhấn nút "Thanh toán".

8. Hệ thống thực hiện một giao dịch (transaction) bao gồm các bước:
   * Lưu thông tin chi tiết Hóa đơn vào CSDL.
   * Gọi `SanPhamBUS` để trừ số lượng tồn kho tương ứng của các sản phẩm đã bán.
   * Cập nhật dữ liệu thống kê để phục vụ cho báo cáo (tác động đến `DoanhThuTheoNgayDTO`, `TopSanPhamDTO`, `LoiNhuanTheoKyDTO`).

9. Hệ thống thông báo "Thanh toán thành công", in hóa đơn (nếu có yêu cầu) và gọi hàm `refreshData()` (từ interface `IRefreshable`) để làm mới lại giao diện `BanHangPanel` cho phiên bán hàng tiếp theo.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Số lượng tồn kho không đủ (Out of Stock)**
  * *Tại bước 3:* Nhân viên nhập số lượng mua lớn hơn số lượng tồn kho hiện tại của sản phẩm.
  * *Hệ thống:* Bắt lỗi logic, không thêm vào giỏ hàng và hiển thị cảnh báo "Số lượng tồn kho không đủ. Hiện chỉ còn [X] sản phẩm".

* **E2: Chưa có sản phẩm trong hóa đơn (Empty Cart)**
  * *Tại bước 7:* Nhân viên nhấn "Thanh toán" nhưng giỏ hàng chưa có sản phẩm nào.
  * *Hệ thống:* Vô hiệu hóa nút thanh toán hoặc hiển thị thông báo "Vui lòng thêm sản phẩm vào hóa đơn trước khi thanh toán".

* **E3: Khách đưa thiếu tiền (Insufficient Payment)**
  * *Tại bước 6 hoặc 7:* Số tiền khách đưa nhỏ hơn tổng giá trị hóa đơn cần thanh toán.
  * *Hệ thống:* Hiển thị thông báo "Số tiền khách đưa không đủ" và chặn tiến trình lưu hóa đơn.

* **E4: Hủy hóa đơn (Cancel Order)**
  * *Tại bất kỳ bước nào trước bước 7:* Nhân viên nhấn nút "Hủy" hoặc xóa toàn bộ sản phẩm khỏi giỏ.
  * *Hệ thống:* Xóa dữ liệu hóa đơn tạm, làm mới giao diện về trạng thái ban đầu (gọi `refreshData()`). Không có dữ liệu nào được ghi xuống CSDL.

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Hóa đơn được tạo, số lượng tồn kho của sản phẩm giảm đúng bằng số lượng đã bán, doanh thu ngày và thống kê khách hàng/sản phẩm bán chạy được cập nhật chính xác vào CSDL.

* **Thất bại:** Hệ thống không ghi nhận bất kỳ thay đổi nào về tồn kho hay doanh thu, đảm bảo tính toàn vẹn dữ liệu (Data Integrity).