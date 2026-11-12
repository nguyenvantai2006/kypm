# TÀI LIỆU ĐẶC TẢ YÊU CẦU SẢN PHẨM (PRD) & QUY CHUẨN KỸ THUẬT
**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM - Desktop Application)
**Mục tiêu:** Định hình kiến trúc, chức năng cốt lõi và các ràng buộc kỹ thuật phục vụ cho việc phát triển và kiểm thử tự động với JUnit 5.

---

## PHẦN I: ĐẶC TẢ YÊU CẦU SẢN PHẨM (PRD)

### 1. Tổng quan Kiến trúc Hệ thống (System Architecture)
Hệ thống được thiết kế theo mô hình 3 lớp (3-tier architecture) truyền thống, kết nối với cơ sở dữ liệu SQL Server.
* **Lớp Giao diện (UI/GUI):** Bao gồm các Panel và Frame điều hướng chính (Ví dụ: `LoginFrame`, `MainFrame`, `BanHangPanel`, `SanPhamPanel`).
* **Lớp Nghiệp vụ (Business/BUS):** Xử lý logic tính toán và xác thực (Ví dụ: `AuthBUS`, `SanPhamBUS`).
* **Lớp Truy xuất Dữ liệu (Data Access/DAO & DTO):** Tương tác với CSDL qua JDBC (`DBConnect.java`), quản lý các thực thể dữ liệu và truy vấn (`NhanVienDAO`, `QuyenDAO`, `SanPhamDAO`, `ThongKeDAO`).

### 2. Các Module cốt lõi và Yêu cầu Nghiệp vụ (Phạm vi Test)

#### 2.1. Module Xác thực & Phân quyền (Authentication & Authorization)
* **Luồng hoạt động:** Nhân viên nhập thông tin tại `LoginFrame`, yêu cầu được gửi tới `AuthBUS` để tra cứu tài khoản trong `NhanVienDAO` và tải quyền từ `QuyenDAO`. Nếu thành công, một phiên làm việc `AuthSession` được tạo ra.
* **Dữ liệu trạng thái (State):** `AuthSession` chứa thông tin `NhanVienDTO` (người dùng hiện tại) và `QuyenDTO` (ma trận quyền hạn).
* **Logic Phân quyền:** Các chức năng (Khách hàng, Voucher, Bán hàng, Hóa đơn) được map với các cờ (flags) phân quyền cụ thể (như `QL_KHACHHANG`, `QL_BANHANG`).
* **Tiêu điểm Test:** Kiểm tra khởi tạo `AuthSession` thành công/thất bại, và tính chính xác của hàm `permissionForKey` trong `MainFrame` dựa trên `QuyenDTO`.

#### 2.2. Module Bán hàng & Dịch vụ Khách hàng (Sales & Customer Service)
* **Tính năng chính:** Thực hiện quy trình bán hàng tại quầy (Point of sale - `BanHangPanel`), quản lý thông tin khách hàng (`KhachHangPanel`), xử lý đổi trả (`DoiTraPanel`) và bảo hành (`BaoHanhPanel`).
* **Tiêu điểm Test:** Đảm bảo dữ liệu khách hàng hoặc các giao dịch đổi/trả được lưu trữ đúng trạng thái; kiểm thử logic cập nhật điểm hoặc hạng của `TopKhachHangDTO` bao gồm số hóa đơn và tổng chi tiêu.

#### 2.3. Module Quản lý Kho & Sản phẩm (Inventory & Catalog)
* **Tính năng chính:** Quản lý danh mục hàng hóa (`SanPhamPanel`), thực hiện nhập hàng và trả hàng nhà cung cấp (`NhapHangPanel`).
* **Luồng hoạt động:** `SanPhamPanel` gọi `SanPhamBUS` để xử lý logic, sau đó `SanPhamBUS` tương tác với `SanPhamDAO` để query dữ liệu.
* **Dữ liệu trạng thái:** Quản lý chi tiết trả hàng cho nhà cung cấp thông qua `ChiTietTraNccDTO` với các trường mã phiếu trả, mã sản phẩm, số lượng và giá nhập.
* **Tiêu điểm Test:** Ràng buộc số lượng (không được âm), giá trị nhập/xuất khớp nhau, logic tính tổng tiền hàng nhập.

#### 2.4. Module Thống kê & Báo cáo (Reports & Statistics)
* **Tính năng chính:** Cung cấp báo cáo doanh thu, lợi nhuận và hiệu suất kinh doanh qua `ThongKePanel`.
* **Luồng hoạt động:** `ThongKePanel` gửi yêu cầu đến `ThongKeDAO`, sau đó truy xuất trực tiếp từ CSDL thông qua `DBConnect`.
* **Dữ liệu trạng thái:** 
  * Doanh thu theo thời gian thực (`DoanhThuTheoNgayDTO`).
  * Tính toán lợi nhuận (`LoiNhuanTheoKyDTO` thực hiện tính Lợi nhuận = Doanh thu - Tiền vốn).
  * Phân tích top bán chạy (`TopSanPhamDTO`) và loại sản phẩm bán chạy (`LoaiSanPhamThongKeDTO`).
* **Tiêu điểm Test:** Logic tính toán lợi nhuận (`getLoiNhuan()`) phải trả về kết quả chính xác dựa trên hàm trừ (`subtract`) của `BigDecimal`. Cần xử lý các trường hợp doanh thu/tiền vốn null bằng cách gán mặc định là `BigDecimal.ZERO`.

---

## PHẦN II: QUY CHUẨN KỸ THUẬT VÀ RÀNG BUỘC HỆ THỐNG

### 1. Kiến trúc Hệ thống (System Architecture)
Hệ thống bắt buộc tuân thủ nghiêm ngặt mô hình 3 lớp (3-Tier Architecture) để đảm bảo tính tách biệt, dễ bảo trì và mở rộng:
* **Lớp Giao diện (Presentation Layer - GUI):** Chỉ chứa các thành phần hiển thị Java Swing (Frame, Panel, Dialog). Không được chứa câu lệnh SQL hoặc logic tính toán nghiệp vụ phức tạp. Mọi tương tác cập nhật dữ liệu thời gian thực trên UI phải thông qua việc implement interface `IRefreshable`.
* **Lớp Nghiệp vụ (Business Logic Layer - BUS):** Chịu trách nhiệm xử lý các quy tắc nghiệp vụ, tính toán, kiểm tra tính hợp lệ của dữ liệu đầu vào (Validation) trước khi đẩy xuống tầng dữ liệu.
* **Lớp Truy cập Dữ liệu (Data Access Layer - DAO):** Chịu trách nhiệm duy nhất về việc giao tiếp với Cơ sở dữ liệu SQL Server thông qua JDBC. Các câu lệnh `SELECT`, `INSERT`, `UPDATE`, `DELETE` chỉ được phép tồn tại ở tầng này.
* **Đối tượng Truyền tải (Data Transfer Object - DTO):** Các class ánh xạ thực thể cơ sở dữ liệu, chỉ dùng để đóng gói và vận chuyển dữ liệu giữa các tầng.

### 2. Cấu trúc Thư mục và Định danh (Directory & Naming Constraints)
* **Chuẩn cấu trúc:** Tổ chức mã nguồn phân tách rõ ràng thư mục code chính và code kiểm thử: `src/main/java` và `src/test/java`.
* **Base Package:** Bắt buộc bắt đầu bằng `com.qlgiay`.
* **Quy hoạch Package:** 
  * `com.qlgiay.gui.frame` / `com.qlgiay.gui.panel`: Chứa các màn hình giao diện.
  * `com.qlgiay.bus`: Chứa các lớp xử lý nghiệp vụ.
  * `com.qlgiay.dao`: Chứa các lớp truy vấn cơ sở dữ liệu.
  * `com.qlgiay.dto`: Chứa các lớp đối tượng thực thể.
  * `com.qlgiay.util`: Chứa các tiện ích dùng chung (như `DBConnect.java`).
* **Quy tắc đặt tên:**
  * Class/Interface: PascalCase (Ví dụ: `TopKhachHangDTO`, `ChiTietTraNccDTO`).
  * Biến/Phương thức: camelCase (Ví dụ: `doanhThu`, `getSoLuongBan()`).
  * Hằng số: UPPER_SNAKE_CASE.

### 3. Ràng buộc về Kiểu Dữ liệu và Xử lý An toàn (Data & Safety Constraints)
* **Kiểu dữ liệu Tiền tệ:** Bắt buộc sử dụng `java.math.BigDecimal` cho mọi trường dữ liệu liên quan đến tài chính (như `giaNhap`, `doanhThu`, `tongChiTieu`, `tienVon`) để tránh sai số thập phân, tuyệt đối không dùng `float` hay `double`.
* **Bảo toàn tính Đóng gói (Encapsulation):** Tất cả các thuộc tính trong các lớp DTO bắt buộc phải là `private` và chỉ được truy xuất/thay đổi thông qua các hàm `getter` và `setter`.
* **Xử lý Null (Null Safety):** Các logic tính toán nghiệp vụ phải kiểm tra giá trị null để tránh `NullPointerException`. Ví dụ, khi thiết lập doanh thu hoặc tiền vốn, nếu tham số truyền vào là null thì phải gán mặc định là `BigDecimal.ZERO`.
* **Quản lý kết nối CSDL:** Lớp `DBConnect` phải quản lý kết nối an toàn. Mọi kết nối (Connection), câu lệnh (Statement), và tập kết quả (ResultSet) phải được đóng (close) trong khối `finally` hoặc sử dụng `try-with-resources`.

### 4. Ràng buộc về Phân quyền và Trạng thái Hệ thống
* **Trạng thái Phiên (Session Management):** Sau khi đăng nhập thành công, hệ thống phải khởi tạo một `AuthSession` duy nhất chứa đầy đủ thông tin nhân viên (`NhanVienDTO`) và bộ cờ quyền hạn (`QuyenDTO`). Đối tượng này sẽ được truyền đi khắp hệ thống để xác thực.
* **Xác thực Điều hướng:** Lớp `MainFrame` hoặc các module điều hướng phải có cơ chế map tính năng (Ví dụ: `KHACHHANG`, `BANHANG`) với cờ phân quyền cụ thể để kiểm duyệt truy cập trước khi render Panel giao diện.

### 5. Yêu cầu Tiêu chuẩn Kiểm thử (Testing Requirements)
* **Framework:** Sử dụng JUnit 5 (thông qua thư viện `junit-jupiter-engine`) làm nền tảng kiểm thử duy nhất cho dự án.
* **Tổ chức Test Class:** Mỗi lớp phức tạp ở tầng BUS, DAO hoặc các hàm logic đặc biệt trong Frame/Panel phải có một lớp test tương ứng.
* **Ràng buộc viết kịch bản Test:**
  * Đánh dấu phương thức kiểm thử bằng annotation `@Test`.
  * Thiết lập môi trường chuẩn (dữ liệu giả, mock object) trước khi test thông qua `@BeforeEach` hoặc `@BeforeAll` để đảm bảo các test case chạy độc lập, không bị ảnh hưởng chéo.
  * Xác minh kết quả bằng Assertion: Bắt buộc dùng `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull` để máy tính tự động so sánh, không dùng `System.out.println` để kiểm tra bằng mắt.
  * Với các trường hợp bắt lỗi (Exception), phải sử dụng `assertThrows` để kiểm chứng xem hệ thống có ném ra đúng lỗi mong muốn khi nhận dữ liệu đầu vào sai hay không.