# Project structure

## Project type

Đây là ứng dụng desktop Java Swing nhiều lớp, không phải web backend. Entry point nằm trong `src/main/java/com/qlgiay/main`; UI ở `gui/panel` và `gui/frame`; nghiệp vụ ở `bus`; truy cập SQL ở `dao`; DTO ở `dto`; kết nối và tiện ích ở `util`.

## Functional domains

- Catalog/sản phẩm và tồn kho.
- Nhập hàng: tạo phiếu nhập, chi tiết nhập, lịch sử và hoàn trả nhà cung cấp.
- Bán hàng: giỏ hàng, hóa đơn, voucher, điểm khách hàng.
- Khách hàng, nhà cung cấp, nhân viên/phân quyền.
- Đổi trả, bảo hành và thống kê.

## Relevant entrypoints

- `NhapHangPanel` (`:72`): ba tab tạo nhập, hoàn trả NCC, lịch sử nhập.
- `BanHangPanel` (`:49`): sản phẩm, giỏ hàng và event `checkout()` gọi BUS.
- `NhapHangBUS.createImport` (`:21`) và `BanHangBUS.createInvoice` (`:30`) là hai boundary nghiệp vụ được phân tích.

## Layer observations

- UI trực tiếp dựng DTO và gọi BUS.
- BUS tự mở JDBC connection, điều phối nhiều DAO và quản lý commit/rollback.
- DAO dùng SQL inline, không có ORM/entity mapping.
- `SanPhamDAO` là shared module có trách nhiệm đọc catalog, tăng/giảm tồn và cập nhật giá.

## Scope evidence

Đã đếm 77 file Java trong `src/main/java`; các package chính gồm `bus`, `dao`, `dto`, `gui`, `main`, `util`. Artifact này ghi cấu trúc hiện trạng, không phải đề xuất tái cấu trúc.

