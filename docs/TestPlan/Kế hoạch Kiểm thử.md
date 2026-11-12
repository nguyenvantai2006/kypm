# KẾ HOẠCH KIỂM THỬ (TEST PLAN)

**Tên dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)

**Công cụ kiểm thử:** JUnit 5 (Java)
**Giai đoạn:** Kiểm thử mức Đơn vị (Unit Testing) và Kiểm thử Tích hợp (Integration Testing)

## 1. Mục đích và Mục tiêu

* **Mục đích:** Xác minh tính chính xác của các luồng nghiệp vụ (Business Logic), đảm bảo an toàn dữ liệu tài chính và tính đúng đắn của cơ chế phân quyền trong hệ thống.

* **Mục tiêu:**

  * Phát hiện sớm các lỗi logic trong quá trình tính toán (ví dụ: tính lợi nhuận, tiền chênh lệch, tồn kho).

  * Đảm bảo hệ thống xử lý đúng các ngoại lệ (Exceptions) khi nhận đầu vào không hợp lệ.

  * Xác nhận hệ thống tuân thủ chặt chẽ kiến trúc 3 lớp (UI không chứa logic, BUS xử lý validation).

## 2. Phạm vi Kiểm thử (Test Scope)

### 2.1. Trong phạm vi (In-Scope)

* **Lớp Đối tượng DTO:** Kiểm tra tính toàn vẹn dữ liệu, các hàm Getter/Setter đặc biệt (xử lý null, tính toán nội bộ như `LoiNhuanTheoKyDTO`).

* **Lớp Nghiệp vụ (BUS):** Kiểm tra logic phân quyền (`AuthBUS`), tính toán nghiệp vụ (`SanPhamBUS`), và các hàm validation (chặn số âm, kiểm tra ngày tháng).

* **Lớp Giao diện (GUI - Logic cụ thể):** Các hàm kiểm tra phân quyền tĩnh (như `MainFramePermissionTest`).

* **Lớp Truy cập dữ liệu (DAO):** (Tùy chọn) Kiểm tra luồng truy vấn cơ sở dữ liệu nếu sử dụng CSDL giả lập (In-memory DB).

### 2.2. Ngoài phạm vi (Out-of-Scope)

* Giao diện người dùng (UI/UX): Độ lệch pixel, màu sắc, hay lỗi hiển thị hình ảnh trên Java Swing.

* Kiểm thử hiệu năng (Performance/Stress Testing) và Kiểm thử bảo mật mạng (Network Security).

* Kiểm thử tương thích hệ điều hành (OS Compatibility).

## 3. Chiến lược Kiểm thử (Test Strategy)

* **Phương pháp pháp tiếp cận:** Black-box (Kiểm thử hộp đen dựa trên Use Case) kết hợp White-box (Kiểm thử hộp trắng bao phủ code logic).

* **Môi trường Code:**

  * Toàn bộ mã kiểm thử được đặt trong thư mục `src/test/java/com/qlgiay/`.

  * Các class kiểm thử phải kết thúc bằng chữ `Test` (Ví dụ: `AuthBUSTest`, `LoiNhuanTheoKyDTOTest`).

* **Kỹ thuật JUnit 5 áp dụng:**

  * `@BeforeEach`: Khởi tạo đối tượng (BUS, DTO) và dữ liệu giả (Mock data) trước mỗi kịch bản test.

  * `@AfterEach`: Dọn dẹp tài nguyên.

  * **Assertions:** Bắt buộc dùng `assertEquals` (đặc biệt cho `BigDecimal`), `assertTrue`, `assertFalse`, `assertNull`.

  * **Exceptions:** Sử dụng `assertThrows` để bắt các lỗi nghiệp vụ chủ động (VD: nhập số lượng âm, sai password).

## 4. Danh sách các Hạng mục Kiểm thử (Test Items)

Dựa trên Đặc tả Use Case, dưới đây là các hạng mục cần viết mã kiểm thử tự động:

### Nhóm 1: Kiểm thử Đối tượng Dữ liệu (DTO Tests)

| Mã | Lớp cần Test | Mô tả kịch bản test chính | 
| ----- | ----- | ----- | 
| T1.1 | `AuthSessionTest` | Kiểm tra khởi tạo session với NhanVienDTO và QuyenDTO hợp lệ. | 
| T1.2 | `LoiNhuanTheoKyDTOTest` | Kiểm tra hàm `getLoiNhuan()`, bắt buộc lợi nhuận = Doanh thu - Tiền vốn. Kiểm tra xử lý null (tự động gán ZERO). | 
| T1.3 | `ChiTietTraNccDTOTest` | Kiểm tra kiểu dữ liệu của Giá nhập phải là `BigDecimal`, Số lượng là `int`. | 

### Nhóm 2: Kiểm thử Nghiệp vụ và Phân quyền (BUS & Security Tests)

| Mã | Lớp cần Test | Mô tả kịch bản test chính | Liên kết UC | 
| ----- | ----- | ----- | ----- | 
| T2.1 | `AuthBUSTest` | Test đăng nhập thành công. Test ngoại lệ sai mật khẩu, tài khoản rỗng. | UC-01 | 
| T2.2 | `MainFramePermissionTest` | Kiểm tra logic map `permissionForKey` khớp với các cờ trong `QuyenDTO`. | UC-02 | 

### Nhóm 3: Kiểm thử Logic Giao dịch (Transaction Logic Tests)

*Khuyến nghị sử dụng Mockito để làm giả lớp DAO khi test lớp BUS.*

| Mã | Lớp cần Test | Mô tả kịch bản test chính | Liên kết UC | 
| ----- | ----- | ----- | ----- | 
| T3.1 | `BanHangBUSTest` | Tính tổng tiền hóa đơn. Bắt lỗi (Exception) khi số lượng mua vượt tồn kho. | UC-03 | 
| T3.2 | `DoiTraBUSTest` | Tính chênh lệch giá trị đổi trả (sử dụng `BigDecimal`). Chặn hóa đơn quá hạn. | UC-05 | 
| T3.3 | `SanPhamBUSTest` | Bắt lỗi Validation khi thêm sản phẩm (giá âm, tên rỗng, trùng SKU). | UC-07 | 

## 5. Môi trường Yêu cầu (Test Environment)

* **JDK:** Java 8 hoặc cao hơn (Tương thích với mã nguồn hiện tại).

* **Build Tool:** Maven (để tự động tải thư viện).

* **Thư viện (Dependencies):**

  * `junit-jupiter-engine` (Version 5.8.2 hoặc mới hơn).

  * `junit-platform-launcher`.

* **Database:** SQL Server (Nếu thực hiện Integration Test kết nối thật).

## 6. Tiêu chí Đạt / Trượt (Pass / Fail Criteria)

* **Tiêu chí Đạt (Pass):** 100% các Test Case trong một bộ (Suite) thực thi thành công (thanh trạng thái chuyển màu xanh). Không có ngoại lệ nào không được xử lý (Unhandled Exceptions).

* **Tiêu chí Trượt (Fail):** Bất kỳ một Test Case nào trả về kết quả sai lệch so với mong đợi hoặc ném ra lỗi hệ thống trong quá trình chạy.