# Đặc tả Use Case: UC-05 - Quản lý Đổi trả hàng hóa (Returns and Exchanges)

## 1. Thông tin chung

* **Mã Use Case:** UC-05

* **Tên Use Case:** Quản lý Đổi trả hàng hóa

* **Tác nhân (Actor):** Nhân viên cửa hàng (Shop staff) có quyền xử lý giao dịch.

* **Mô tả ngắn gọn:** Use case này cho phép nhân viên tiếp nhận yêu cầu đổi hoặc trả lại sản phẩm từ khách hàng đã mua trước đó. Quá trình này sẽ đối soát hóa đơn cũ, tính toán mức giá chênh lệch (nếu có) và cập nhật lại số lượng tồn kho cũng như thống kê doanh thu.

## 2. Tiền điều kiện (Preconditions)

* Nhân viên đang duy trì phiên làm việc `AuthSession` hợp lệ.

* Tài khoản của nhân viên có cờ phân quyền hợp lệ để truy cập chức năng Đổi trả.

* Giao diện `DoiTraPanel` đã được tải lên thành công trên vùng hiển thị của `MainFrame`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

1. Nhân viên tiếp nhận sản phẩm và yêu cầu đổi/trả từ khách hàng tại màn hình `DoiTraPanel`.

2. Nhân viên nhập mã hóa đơn gốc vào ô tìm kiếm để truy xuất thông tin.

3. Hệ thống kiểm tra trong cơ sở dữ liệu và hiển thị chi tiết hóa đơn (bao gồm danh sách sản phẩm, số lượng, giá bán, và ngày mua) thông qua các lớp DAO tương ứng.

4. Nhân viên chọn sản phẩm mà khách hàng muốn trả lại và nhập số lượng trả.

5. *(Trường hợp Đổi hàng)* Nhân viên chọn tiếp sản phẩm mới mà khách hàng muốn lấy. Hệ thống gọi lớp BUS để tính toán khoản tiền chênh lệch (khách cần bù thêm tiền hoặc cửa hàng thối lại tiền). Phép tính này bắt buộc sử dụng đối tượng `BigDecimal`.

6. Nhân viên nhấn nút "Xác nhận đổi/trả".

7. Hệ thống thực hiện một giao dịch (transaction) cơ sở dữ liệu qua `DBConnect`:

   * Tạo phiếu đổi trả mới.

   * Cộng lại số lượng tồn kho cho sản phẩm bị trả.

   * Trừ số lượng tồn kho cho sản phẩm khách đổi lấy mới.

   * Điều chỉnh lại số liệu tài chính, tác động trực tiếp đến tính toán của `DoanhThuTheoNgayDTO` và `LoiNhuanTheoKyDTO` (khấu trừ doanh thu/lợi nhuận đã ghi nhận trước đó).

8. Hệ thống thông báo thành công và gọi hàm `refreshData()` (từ interface `IRefreshable`) để làm trắng form, sẵn sàng cho giao dịch tiếp theo.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Hóa đơn không tồn tại (Invoice Not Found)**

  * *Tại bước 3:* Mã hóa đơn nhân viên nhập vào không có trong cơ sở dữ liệu SQL Server.

  * *Hệ thống:* Hiển thị thông báo "Không tìm thấy hóa đơn. Vui lòng kiểm tra lại mã".

* **E2: Vi phạm chính sách đổi trả (Policy Violation)**

  * *Tại bước 3:* Hóa đơn được tìm thấy nhưng đã quá thời hạn cho phép đổi trả (ví dụ: quá 7 ngày).

  * *Hệ thống:* Lớp nghiệp vụ (BUS) từ chối thao tác, hiển thị cảnh báo "Hóa đơn này đã quá thời hạn đổi trả theo quy định".

* **E3: Vượt quá số lượng mua gốc (Invalid Quantity)**

  * *Tại bước 4:* Nhân viên nhập số lượng trả lớn hơn số lượng khách đã mua trong hóa đơn gốc.

  * *Hệ thống:* Bắt lỗi logic, hiển thị thông báo "Số lượng trả không được lớn hơn số lượng đã mua".

* **E4: Sản phẩm đổi hết hàng (Out of Stock for Exchange)**

  * *Tại bước 5:* Sản phẩm khách muốn đổi lấy mới không đủ số lượng trong kho.

  * *Hệ thống:* Hiển thị cảnh báo "Sản phẩm đổi không đủ số lượng tồn kho".

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Giao dịch đổi/trả được lưu trữ. Dữ liệu tồn kho (`SanPhamDAO`) và các báo cáo thống kê (`ThongKeDAO`) được hệ thống tự động cân đối lại để phản ánh đúng thực tế tài chính và kho hàng.

* **Thất bại:** Không có bất kỳ thay đổi nào được ghi xuống CSDL, đảm bảo tính nhất quán của dữ liệu.