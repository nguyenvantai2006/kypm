# Data model

## Entity inventory

- `SAN_PHAM`: master sản phẩm; `SoLuong` là tồn tổng hợp, `DonGia` là giá bán hiện hành, `PhanTramLoiNhuan` là tỷ lệ tính giá.
- `PHIEU_NHAP`: header nhập, liên kết nhân viên và nhà cung cấp, giữ tổng số mặt hàng/tổng tiền.
- `CHI_TIET_PHIEU_NHAP`: dòng nhập theo `(MaPN, MaSP)`, giữ `SoLuong` và `GiaNhap`.
- `HOA_DON`: header bán, liên kết nhân viên, tùy chọn khách hàng và voucher, giữ tổng tiền cuối.
- `CHI_TIET_HOA_DON`: dòng bán theo `(MaHD, MaSP)`, giữ số lượng và đơn giá bán đã chốt.
- `VOUCHER`: cấu hình và số lượng voucher; `HOA_DON.MaVoucher` là FK tùy chọn.
- `KHACH_HANG`: hồ sơ và điểm tích lũy; được cập nhật trong bán hàng.

## Relationships

`NHA_CUNG_CAP 1-N PHIEU_NHAP`; `NHAN_VIEN 1-N PHIEU_NHAP` và `HOA_DON`; `PHIEU_NHAP 1-N CHI_TIET_PHIEU_NHAP`; `SAN_PHAM 1-N CHI_TIET_PHIEU_NHAP`; `HOA_DON 1-N CHI_TIET_HOA_DON`; `SAN_PHAM 1-N CHI_TIET_HOA_DON`; `HOA_DON N-1 KHACH_HANG` và `N-1 VOUCHER`.

## Current storage semantics

- Tồn kho được lưu cục bộ tại `SAN_PHAM.SoLuong`, không tách theo lô.
- Giá bán hiện hành nằm tại `SAN_PHAM.DonGia`.
- Giá nhập lịch sử nằm tại `CHI_TIET_PHIEU_NHAP.GiaNhap`, nhưng mỗi sản phẩm chỉ có một dòng trong mỗi phiếu nhập.
- `SanPhamDAO.SELECT_ALL` lấy `GiaNhap` của lần nhập mới nhất để dựng DTO; `updateDerivedSalePrice` ghi giá bán theo lần nhập hiện tại.
- Không có khóa lô, số lượng còn lại theo lô, ngày nhập độc lập ở detail, hoặc bảng phân bổ dòng bán vào lô.

## Schema anchors

`Init_Schema.sql:70-145` định nghĩa năm bảng trọng tâm; PK detail là composite `(MaPN, MaSP)` và `(MaHD, MaSP)`.

