# Đặc tả Use Case: UC-09 - Thống kê và Báo cáo (Reports & Statistics)

## 1. Thông tin chung

* **Mã Use Case:** UC-09

* **Tên Use Case:** Thống kê và Báo cáo

* **Tác nhân (Actor):** Quản lý cửa hàng hoặc Chủ cửa hàng (người được cấp quyền cao nhất về dữ liệu).

* **Mô tả ngắn gọn:** Phân hệ này tổng hợp dữ liệu từ các giao dịch Bán hàng (UC-03), Đổi trả (UC-05) và Nhập hàng (UC-08) để tính toán doanh thu, lợi nhuận, và xếp hạng hiệu quả kinh doanh của sản phẩm cũng như khách hàng.

## 2. Tiền điều kiện (Preconditions)

* Tác nhân đang duy trì phiên làm việc `AuthSession` hợp lệ.

* Tài khoản của tác nhân có cờ phân quyền truy cập chức năng Thống kê (`QL_THONGKE`).

* Giao diện `ThongKePanel` đã được tải và hiển thị thành công thông qua thanh điều hướng của `MainFrame`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Tác nhân nhấp vào menu "Thống kê" trên màn hình chính `MainFrame`.

2. `MainFrame` kiểm tra quyền truy cập và điều hướng tải `ThongKePanel`.

3. Tác nhân chọn tiêu chí thống kê trên giao diện (Ví dụ: Theo ngày, Theo tháng, Theo quý, hoặc Top Sản phẩm/Khách hàng).

4. `ThongKePanel` gửi yêu cầu truy xuất dữ liệu (`requests report data`) đến lớp `ThongKeDAO`.

5. `ThongKeDAO` mở kết nối JDBC qua `DBConnect`, thực thi các câu lệnh `SELECT`, `GROUP BY`, `SUM()` phức tạp trong cơ sở dữ liệu SQL Server.

6. Hệ thống ánh xạ dữ liệu thô từ CSDL vào các đối tượng DTO chuyên biệt:

   * **Doanh thu:** Đóng gói vào `DoanhThuTheoNgayDTO` (gồm ngày và tổng doanh thu).

   * **Lợi nhuận:** Đóng gói vào `LoiNhuanTheoKyDTO`. Hệ thống thiết lập Doanh thu và Tiền vốn, sau đó tự động tính Lợi nhuận qua hàm `getLoiNhuan()` (Lợi nhuận = Doanh thu - Tiền vốn).

   * **Sản phẩm:** Đóng gói vào `TopSanPhamDTO` (Mã SP, Tên SP, Số lượng bán, Doanh thu) và `LoaiSanPhamThongKeDTO` (Loại SP, Số lượng bán) để xem mặt hàng nào bán chạy nhất.

   * **Khách hàng:** Đóng gói vào `TopKhachHangDTO` (Mã KH, Tên KH, Số hóa đơn, Tổng chi tiêu) để tri ân khách VIP.

7. Danh sách các DTO được trả về giao diện. `ThongKePanel` vẽ biểu đồ (Chart) hoặc hiển thị bảng dữ liệu (Table) trực quan cho Quản lý xem.

8. *(Tùy chọn)* Quản lý nhấn nút "Xuất báo cáo" (Export to Excel/PDF). Hệ thống kết xuất file tương ứng. Use Case kết thúc.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Không có dữ liệu trong kỳ (No Data)**

  * *Tại bước 7:* Nếu khoảng thời gian người dùng chọn không phát sinh bất kỳ giao dịch nào.

  * *Hệ thống:* Giao diện không vẽ biểu đồ, hiển thị thông báo "Không có dữ liệu thống kê cho khoảng thời gian này".

* **E2: Xử lý giá trị rỗng trong tính toán Lợi nhuận (Null Value Handling)**

  * *Tại bước 6:* Dữ liệu truy vấn trả về NULL cho Doanh thu hoặc Tiền vốn (ví dụ kỳ đó chỉ có bán mà không có nhập, hoặc ngược lại).

  * *Hệ thống:* Phương thức `setter` của `LoiNhuanTheoKyDTO` tự động bắt trường hợp giá trị `null` và gán thành `BigDecimal.ZERO` để đảm bảo hàm trừ `subtract` không bị văng lỗi `NullPointerException`.

* **E3: Lỗi thực thi truy vấn (Query Execution Error)**

  * *Tại bước 5:* Lỗi kết nối CSDL hoặc câu lệnh SQL phức tạp bị quá thời gian (timeout).

  * *Hệ thống:* Lớp DAO bắt lỗi `SQLException`, trả về danh sách rỗng và giao diện hiển thị cảnh báo "Lỗi truy xuất dữ liệu từ máy chủ. Vui lòng thử lại sau".

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Tác nhân xem được báo cáo chính xác, cung cấp cái nhìn tổng quan về tình hình kinh doanh của cửa hàng.

* **Thất bại:** Hệ thống báo lỗi an toàn, hoàn toàn không làm sai lệch hay tác động đến bất kỳ dữ liệu gốc nào trong CSDL.