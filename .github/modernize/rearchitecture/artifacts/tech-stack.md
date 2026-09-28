# Tech stack

- Java source/target 24 theo `pom.xml`.
- Maven project `QuanLyCuaHangGiay`.
- Swing/AWT desktop UI; FlatLaf 3.5.4.
- JDBC kết nối SQL Server qua `mssql-jdbc` 13.2.1.jre11.
- iTextPDF 5.5.13.3 và Apache POI 5.2.5 cho xuất tài liệu.
- JGoodDatePicker 11.2.1, JFreeChart 1.5.6, Commons Text 1.12.0, JUnit Jupiter 5.10.2.
- Kiến trúc persistence là DAO + SQL text block; transaction được tạo thủ công trong BUS.

## Runtime constraints observed

- `NhapHangBUS` và `BanHangBUS` dùng synchronous JDBC.
- Không thấy annotation DI, ORM, REST route hoặc message broker ở các luồng được phân tích.
- Isolation level và row locking không được chỉ định rõ; FIFO sẽ cần xác định lại concurrency contract.

