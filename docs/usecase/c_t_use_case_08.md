# Đặc tả Use Case: UC-08 - Quản lý Nhập hàng & Trả hàng cho Nhà cung cấp (Purchasing and supplier returns)

## 1. Thông tin chung

* **Mã Use Case:** UC-08

* **Tên Use Case:** Quản lý Nhập hàng và Trả hàng cho Nhà cung cấp

* **Tác nhân (Actor):** Nhân viên kho, Quản lý cửa hàng (người có quyền kiểm soát dòng hàng hóa).

* **Mô tả ngắn gọn:** Use case này quản lý luồng hàng hóa đầu vào của cửa hàng. Tác nhân có thể tạo phiếu nhập hàng mới (cộng tồn kho) hoặc tạo phiếu trả hàng cho nhà cung cấp (trừ tồn kho và thu hồi vốn).

## 2. Tiền điều kiện (Preconditions)

* Tác nhân đang duy trì phiên làm việc `AuthSession` hợp lệ.

* Tác nhân được phân quyền truy cập chức năng Nhập hàng trong `QuyenDTO`.

* Giao diện `NhapHangPanel` đã được tải và hiển thị thông qua menu điều hướng của `MainFrame`.

## 3. Luồng sự kiện chính (Basic Flow / Happy Path)

*Luồng 1: Nhập hàng (Importing)*
1. Nhân viên nhấp vào chức năng Nhập hàng trên `NhapHangPanel`.
2. Hệ thống hiển thị form tạo Phiếu nhập. Nhân viên chọn Nhà cung cấp và bắt đầu thêm các sản phẩm cần nhập vào danh sách.
3. Với mỗi sản phẩm, nhân viên nhập Số lượng và Giá nhập (`giaNhap`). 
4. Hệ thống tự động tính toán tổng tiền của Phiếu nhập.
5. Nhân viên xác nhận và nhấn "Lưu phiếu nhập".
6. Hệ thống gọi các lớp DAO để lưu phiếu nhập và cộng dồn số lượng tương ứng vào tồn kho hiện tại của sản phẩm.

*Luồng 2: Trả hàng cho Nhà cung cấp (Returning to Supplier)*
1. Nhân viên chuyển sang tab/chức năng Trả hàng trên `NhapHangPanel`.
2. Hệ thống khởi tạo danh sách các chi tiết trả hàng dựa trên đối tượng `ChiTietTraNccDTO`.
3. Nhân viên điền thông tin phiếu trả: nhập Mã phiếu trả (`maPT`), chọn Mã sản phẩm (`maSP`), nhập Số lượng trả (`soLuong`) và Giá nhập hoàn lại (`giaNhap`). (Lưu ý: `giaNhap` bắt buộc sử dụng kiểu `BigDecimal` để tính toán chính xác).
4. Nhân viên xác nhận và nhấn "Tạo phiếu trả".
5. Hệ thống thực hiện giao dịch (transaction): lưu thông tin `ChiTietTraNccDTO` vào CSDL và trừ đi số lượng tồn kho tương ứng của các sản phẩm đó.
6. Hệ thống thông báo thành công và gọi hàm `refreshData()` (từ `IRefreshable`) để làm sạch form và cập nhật lại lưới dữ liệu. Use Case kết thúc.

## 4. Các luồng rẽ nhánh / Ngoại lệ (Alternative & Exception Flows)

* **E1: Thiếu thông tin hoặc Định dạng sai (Invalid Inputs)**
  * *Tại bước 3 (cả 2 luồng):* Nhân viên bỏ trống `maSP`, hoặc nhập số lượng/giá nhập là số âm.
  * *Hệ thống:* Lớp nghiệp vụ (BUS) từ chối thực thi, hiển thị cảnh báo "Vui lòng nhập đầy đủ Mã sản phẩm. Số lượng và Giá nhập phải lớn hơn 0".

* **E2: Số lượng trả vượt mức tồn kho (Return Quantity Exceeds Stock)**
  * *Tại bước 3 (Luồng 2):* Nhân viên nhập `soLuong` trong `ChiTietTraNccDTO` lớn hơn số lượng tồn kho hiện tại của sản phẩm đó.
  * *Hệ thống:* Lớp BUS kiểm tra logic và chặn lại, hiển thị thông báo "Số lượng trả nhà cung cấp không được lớn hơn tồn kho hiện tại của cửa hàng".

* **E3: Lỗi kết nối Cơ sở dữ liệu (Database Error)**
  * *Tại bước 5:* Lớp `DBConnect` mất kết nối hoặc có lỗi khóa ngoại (Foreign Key) khi insert dữ liệu.
  * *Hệ thống:* Hủy toàn bộ giao dịch (Rollback), ném ra Exception và thông báo "Lỗi hệ thống khi lưu dữ liệu. Vui lòng thử lại".

## 5. Hậu điều kiện (Postconditions)

* **Thành công:** Tồn kho sản phẩm được cập nhật (tăng lên nếu Nhập hàng, giảm xuống nếu Trả hàng). Giá trị vốn hàng hóa (tiền vốn) được ghi nhận để làm cơ sở cho `LoiNhuanTheoKyDTO` tính toán lợi nhuận sau này.

* **Thất bại:** Tồn kho không bị thay đổi, không có dữ liệu sai lệch nào được ghi nhận.