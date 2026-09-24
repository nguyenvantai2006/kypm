USE [QuanLyCuaHangGiay]
GO

SET XACT_ABORT ON
GO

BEGIN TRY
    BEGIN TRANSACTION

    -- ==============================================================================
    -- 1. XÓA DỮ LIỆU CŨ (Mở rộng để xóa sạch 20 record chuẩn bị nạp)
    -- ==============================================================================
    DELETE FROM dbo.CHI_TIET_TRA_NCC;
    DELETE FROM dbo.PHIEU_TRA_NCC;
    DELETE FROM dbo.DOI_TRA;
    DELETE FROM dbo.PHIEU_BAO_HANH;
    DELETE FROM dbo.CHI_TIET_HOA_DON;
    DELETE FROM dbo.HOA_DON;
    DELETE FROM dbo.CHI_TIET_PHIEU_NHAP;
    DELETE FROM dbo.PHIEU_NHAP;
    
    DELETE FROM dbo.VOUCHER;
    DELETE FROM dbo.SAN_PHAM;
    DELETE FROM dbo.KHACH_HANG;
    DELETE FROM dbo.NHA_CUNG_CAP;
    
    -- Xóa các danh mục nội bộ (Chỉ dọn dẹp để insert lại, không thay đổi data gốc của bạn)
    DELETE FROM dbo.NHAN_VIEN;
    DELETE FROM dbo.QUYEN;

    -- ==============================================================================
    -- 2. DỮ LIỆU NỘI BỘ (Giữ nguyên tuyệt đối theo yêu cầu)
    -- ==============================================================================
    INSERT INTO dbo.QUYEN (MaQuyen, TenQuyen, QL_BanHang, QL_KhachHang, QL_SanPham, QL_NhapHang, QL_NhanVien, QL_ThongKe)
    VALUES
        (N'Q01', N'Quản trị viên', 0, 0, 0, 0, 1, 0),
        (N'Q02', N'Quản lý', 1, 1, 1, 1, 0, 1),
        (N'Q03', N'Nhân viên', 1, 1, 1, 0, 0, 0),
        (N'TQ001', N'Quản trị mẫu', 1, 1, 1, 1, 1, 1),
        (N'TQ002', N'Quản lý mẫu', 1, 1, 1, 1, 0, 1),
        (N'TQ003', N'Nhân viên mẫu', 1, 1, 1, 0, 0, 0);

    INSERT INTO dbo.NHAN_VIEN (MaNV, Ho, Ten, MaQuyen, TaiKhoan, MatKhau, Luong, TrangThai)
    VALUES
        (N'NV001', N'Admin', N'System', N'Q01', N'admin', N'123456', 20000000, 1),
        (N'NV002', N'Nguyễn', N'An', N'Q01', N'an.nguyen', N'123456', 10000000, 1),
        (N'NV003', N'Trần', N'Bình', N'Q02', N'binh.tran', N'123456', 10000000, 1),
        (N'NV004', N'Lê', N'Cường', N'Q02', N'cuong.le', N'123456', 9500000, 1),
        (N'NV005', N'Phạm', N'Dũng', N'Q03', N'dung.pham', N'123456', 8500000, 1),
        (N'NV006', N'Hoàng', N'Hà', N'Q03', N'ha.hoang', N'123456', 8000000, 1),
        (N'NV007', N'Đặng', N'Khoa', N'Q02', N'khoa.dang', N'123456', 10500000, 1),
        (N'NV008', N'Bùi', N'Long', N'Q03', N'long.bui', N'123456', 7800000, 1),
        (N'NV009', N'Vũ', N'Minh', N'Q02', N'minh.vu', N'123456', 9900000, 1),
        (N'NV010', N'Phan', N'Nam', N'Q03', N'nam.phan', N'123456', 8200000, 1),
        (N'NV011', N'Ngô', N'Quang', N'Q02', N'quang.ngo', N'123456', 11000000, 1),
        (N'TNV001', N'Nguyễn', N'Quản Trị', N'TQ001', N'test.admin', N'123456', 18000000, 1),
        (N'TNV002', N'Trần', N'Bán Hàng', N'TQ002', N'test.manager', N'123456', 12000000, 1),
        (N'TNV003', N'Lê', N'Kho Hàng', N'TQ003', N'test.staff', N'123456', 9000000, 1);


    -- ==============================================================================
    -- 3. KHÁCH HÀNG THỰC TẾ (20 Khách Hàng)
    -- ==============================================================================
    INSERT INTO dbo.KHACH_HANG (MaKH, TenKH, SDT, DiaChi, DiemTichLuy, TrangThai)
    VALUES
        (N'KH001', N'Nguyễn Văn A', N'0901000001', N'12 Lê Lợi, Quận 1, TP.HCM', 150, 1),
        (N'KH002', N'Trần Thị B', N'0901000002', N'45 Nguyễn Trãi, Quận 5, TP.HCM', 0, 1),
        (N'KH003', N'Lê Hải C', N'0901000003', N'78 Điện Biên Phủ, Bình Thạnh, TP.HCM', 500, 1),
        (N'KH004', N'Phạm Minh D', N'0901000004', N'32 Võ Văn Ngân, Thủ Đức, TP.HCM', 50, 1),
        (N'KH005', N'Vũ Đức E', N'0901000005', N'65 Quang Trung, Gò Vấp, TP.HCM', 1200, 1),
        (N'KH006', N'Đinh Thanh F', N'0901000006', N'98 Lũy Bán Bích, Tân Phú, TP.HCM', 0, 1),
        (N'KH007', N'Bùi Tấn G', N'0901000007', N'Vinhome Central Park, Bình Thạnh, TP.HCM', 350, 1),
        (N'KH008', N'Đỗ Hoàng H', N'0901000008', N'112 Phan Đăng Lưu, Phú Nhuận, TP.HCM', 10, 1),
        (N'KH009', N'Hồ Trọng I', N'0901000009', N'22 Lê Duẩn, Quận 1, TP.HCM', 200, 1),
        (N'KH010', N'Ngô Văn K', N'0901000010', N'55 Hai Bà Trưng, Quận 3, TP.HCM', 0, 1),
        (N'KH011', N'Dương Thị L', N'0901000011', N'77 Lê Văn Sỹ, Tân Bình, TP.HCM', 75, 1),
        (N'KH012', N'Lý Công M', N'0901000012', N'88 Cách Mạng Tháng 8, Quận 10, TP.HCM', 400, 1),
        (N'KH013', N'Đào Bá N', N'0901000013', N'99 Phạm Văn Đồng, Thủ Đức, TP.HCM', 0, 1),
        (N'KH014', N'Đoàn Tú O', N'0901000014', N'111 Nguyễn Đình Chiểu, Quận 3, TP.HCM', 120, 1),
        (N'KH015', N'Vương Tấn P', N'0901000015', N'222 Võ Thị Sáu, Quận 3, TP.HCM', 600, 1),
        (N'KH016', N'Trịnh Thu Q', N'0901000016', N'333 Xô Viết Nghệ Tĩnh, Bình Thạnh, TP.HCM', 0, 1),
        (N'KH017', N'Đinh Việt R', N'0901000017', N'444 Kinh Dương Vương, Bình Tân, TP.HCM', 300, 1),
        (N'KH018', N'Lâm Hà S', N'0901000018', N'555 Hậu Giang, Quận 6, TP.HCM', 80, 1),
        (N'KH019', N'Mai Quốc T', N'0901000019', N'666 Nguyễn Văn Linh, Quận 7, TP.HCM', 1000, 1),
        (N'KH020', N'Phan Văn U', N'0901000020', N'777 Huỳnh Tấn Phát, Quận 7, TP.HCM', 50, 1);


    -- ==============================================================================
    -- 4. NHÀ CUNG CẤP THỰC TẾ
    -- ==============================================================================
    INSERT INTO dbo.NHA_CUNG_CAP (MaNCC, TenNCC, SDT, DiaChi, TrangThai)
    VALUES
        (N'NCC001', N'Nike Vietnam', N'0910000001', N'Bitexco Tower, Quận 1, TP.HCM', 1),
        (N'NCC002', N'Adidas Vietnam', N'0910000002', N'Landmark 81, Bình Thạnh, TP.HCM', 1),
        (N'NCC003', N'Puma South East Asia', N'0910000003', N'Crescent Mall, Quận 7, TP.HCM', 1),
        (N'NCC004', N'Vans & Converse Distributor', N'0910000004', N'Aeon Mall Tân Phú, TP.HCM', 1),
        (N'NCC005', N'Biti''s Corporation', N'0910000005', N'Quận 6, TP.HCM', 1);


    -- ==============================================================================
    -- 5. SẢN PHẨM THỰC TẾ (20 Sản Phẩm, SKU: SP-SIZE-COLOR)
    -- ==============================================================================
    INSERT INTO dbo.SAN_PHAM (MaSP, TenSP, LoaiSP, DonViTinh, SoLuong, DonGia, PhanTramLoiNhuan, MauSac, Size, ChatLieu, ThuongHieu, NuocSanXuat, NgaySanXuat, MoTa, HinhAnh, TrangThai,maNCC)
    VALUES
        (N'SP001-42-DEN', N'Nike Air Force 1', N'Giày Thể Thao', N'Đôi', 25, 0, 20, N'Đen', N'42', N'Da Bò', N'Nike', N'Mỹ', '2025-05-10', N'Mẫu giày quốc dân không bao giờ lỗi mốt.', N'nike.jpg', 1,NCC001),
        (N'SP002-41-TRANG', N'Adidas Ultraboost 22', N'Giày Chạy Bộ', N'Đôi', 30, 0, 20, N'Trắng', N'41', N'Vải Primeknit', N'Adidas', N'Đức', '2025-08-15', N'Đế boost êm ái, hỗ trợ chạy bộ chuyên nghiệp.', N'chelseaboot.jpg', 1,NCC002),
        (N'SP003-40-DEN', N'Vans Old Skool', N'Giày Sneaker', N'Đôi', 50, 0, 20, N'Đen', N'40', N'Vải Canvas', N'Vans', N'Mỹ', '2025-01-20', N'Giày trượt ván cổ điển, phối đồ cực dễ.', N'vansold.jpg', 1,NCC004),
        (N'SP004-43-TRANG', N'Converse Chuck 70', N'Giày Sneaker', N'Đôi', 40, 0, 20, N'Trắng', N'43', N'Vải Canvas', N'Converse', N'Mỹ', '2025-02-14', N'Chất vải dày dặn, form chuẩn vintage.', N'converse.jpg', 1,NCC003),
        (N'SP005-39-TRANG', N'Puma RS-X', N'Giày Thể Thao', N'Đôi', 20, 0, 20, N'Trắng', N'39', N'Vải Mesh', N'Puma', N'Đức', '2025-07-22', N'Thiết kế chunky khỏe khoắn, năng động.', N'puma.jpg', 1,NCC004),
        (N'SP006-42-DEN', N'Bitis Hunter X', N'Giày Chạy Bộ', N'Đôi', 60, 0, 20, N'Đen', N'42', N'Vải LiteKnit', N'Biti''s', N'Việt Nam', '2026-01-05', N'Tự hào thương hiệu Việt, đế siêu nhẹ.', N'bitis.jpg', 1,NCC001),
        (N'SP007-41-XANH', N'Nike Air Max 97', N'Giày Thể Thao', N'Đôi', 15, 0, 20, N'Xanh', N'41', N'Vải tổng hợp', N'Nike', N'Mỹ', '2025-11-11', N'Dải phản quang cực chất, êm ái đàn hồi.', N'nike.jpg', 1,NCC003),
        (N'SP008-40-TRANG', N'Adidas Stan Smith', N'Giày Sneaker', N'Đôi', 45, 0, 20, N'Trắng', N'40', N'Da PU', N'Adidas', N'Đức', '2025-04-12', N'Basic, lịch sự và phù hợp mọi lứa tuổi.', N'adias1.jpg', 1,NCC001),
        (N'SP009-42-DEN', N'Giày Oxford Classic', N'Giày Tây', N'Đôi', 10, 0, 30, N'Đen', N'42', N'Da Bò Thật', N'No Brand', N'Việt Nam', '2025-09-09', N'Lịch lãm, phong cách công sở sang trọng.', N'oxford.jpg', 1,NCC001),
        (N'SP010-41-NAU', N'Giày Lười Loafer', N'Giày Lười', N'Đôi', 18, 0, 30, N'Nâu', N'41', N'Da Sáp', N'No Brand', N'Việt Nam', '2025-10-25', N'Dễ mang, thoải mái cho mùa hè.', N'Giay_Luoi_Loafer.png', 1,NCC002),
        (N'SP011-38-HONG', N'MLB Boston Chunky', N'Giày Sneaker', N'Đôi', 22, 0, 20, N'Hồng', N'38', N'Da Tổng Hợp', N'MLB', N'Hàn Quốc', '2025-12-01', N'Hack dáng 5cm, phong cách Hàn Quốc.', N'boston.jpg', 1,NCC003),
        (N'SP012-42-XAM', N'New Balance 574', N'Giày Chạy Bộ', N'Đôi', 30, 0, 20, N'Xám', N'42', N'Da Lộn', N'New Balance', N'Mỹ', '2025-06-15', N'Phong cách retro, cực kỳ êm chân.', N'balnce.jpg', 1,NCC002),
        (N'SP013-40-TRANG', N'Fila Disruptor 2', N'Giày Sneaker', N'Đôi', 15, 0, 20, N'Trắng', N'40', N'Da PU', N'Fila', N'Hàn Quốc', '2025-03-08', N'Chunky sneaker cá tính cho phái nữ.', N'fila.jpg', 1,NCC003),
        (N'SP014-41-DEN', N'Asics Gel Kayano', N'Giày Chạy Bộ', N'Đôi', 25, 0, 20, N'Đen', N'41', N'Vải Mesh', N'Asics', N'Nhật Bản', '2026-02-14', N'Bảo vệ cổ chân cực tốt khi chạy đường dài.', N'asic.jpg', 1,NCC004),
        (N'SP015-43-DO', N'Nike Air Jordan 1', N'Giày Bóng Rổ', N'Đôi', 10, 0, 20, N'Đỏ', N'43', N'Da Tổng Hợp', N'Nike', N'Mỹ', '2025-09-30', N'Biểu tượng của văn hóa sát mặt đất.', N'jordan.jpg', 1,NCC003),
        (N'SP016-39-TRANG', N'Reebok Club C 85', N'Giày Thể Thao', N'Đôi', 20, 0, 20, N'Trắng', N'39', N'Da Bò', N'Reebok', N'Anh Quốc', '2025-11-20', N'Tinh giản, thanh lịch, độ bền cao.', N'Rebbok.jpg', 1,NCC003),
        (N'SP017-42-DEN', N'Giày Derby Nam', N'Giày Tây', N'Đôi', 12, 0, 30, N'Đen', N'42', N'Da Bò Thật', N'No Brand', N'Việt Nam', '2026-01-15', N'Mẫu Derby basic phù hợp mọi dịp tiệc tùng.', N'derby.jpg', 1,NCC003),
        (N'SP018-40-NAU', N'Chelsea Boot', N'Giày Boot', N'Đôi', 15, 0, 30, N'Nâu', N'40', N'Da Lộn', N'No Brand', N'Việt Nam', '2025-12-10', N'Cổ cao, nam tính, cực chất khi mặc jeans.', N'chelseaboot.jpg', 1,NCC004),
        (N'SP019-41-XANH', N'Mizuno Wave Rider', N'Giày Chạy Bộ', N'Đôi', 20, 0, 20, N'Xanh', N'41', N'Vải Mesh', N'Mizuno', N'Nhật Bản', '2026-01-22', N'Đế Wave đàn hồi, thích hợp chạy track.', N'mizuno.jpg', 1,NCC004),
        (N'SP020-42-DEN', N'Under Armour Curry', N'Giày Bóng Rổ', N'Đôi', 15, 0, 20, N'Đen', N'42', N'Vải Knit', N'Under Armour', N'Mỹ', '2025-10-10', N'Bám sàn cực tốt, chuyên dụng cho bóng rổ.', N'under.jpg', 1,NCC001);

    -- ==============================================================================
    -- 6. VOUCHER KHUYẾN MÃI
    -- ==============================================================================
    INSERT INTO dbo.VOUCHER (MaVoucher, TenVoucher, PhanTramGiam, SoTienGiam, GiamToiDa, DieuKienApDung, NgayBatDau, NgayKetThuc, SoLuong, TrangThai)
    VALUES
        (N'WELCOME20', N'Giảm 20% Cho Khách Mới', 20, NULL, 300000, 500000, '2026-01-01', '2026-12-31', 100, 1),
        (N'GIAM50K', N'Giảm Trực Tiếp 50K', 0, 50000, NULL, 300000, '2026-01-01', '2026-12-31', 500, 1),
        (N'BLACKFRIDAY', N'Black Friday Sale 50%', 50, NULL, 500000, 1000000, '2026-11-20', '2026-11-30', 50, 1);

    -- ==============================================================================
    -- 7. NHẬP HÀNG TỪ NHÀ CUNG CẤP (5 Phiếu nhập bao trọn 20 sản phẩm)
    -- ==============================================================================
    INSERT INTO dbo.PHIEU_NHAP (MaPN, MaNV, MaNCC, NgayNhap, TongSoMatHang, TongTien)
    VALUES
        (N'PN001', N'TNV003', N'NCC001', '2026-01-05', 4, 114000000), -- Nike
        (N'PN002', N'TNV003', N'NCC002', '2026-01-10', 4, 185000000), -- Adidas & Khác
        (N'PN003', N'TNV002', N'NCC003', '2026-01-15', 4, 102000000), -- Puma
        (N'PN004', N'TNV002', N'NCC004', '2026-01-20', 4, 88000000),  -- Vans & Converse
        (N'PN005', N'TNV003', N'NCC005', '2026-01-25', 4, 76000000);  -- Nội địa / Tây

    -- Cập nhật Giá Nhập chân thực cho 20 Sản phẩm
    INSERT INTO dbo.CHI_TIET_PHIEU_NHAP (MaPN, MaSP, SoLuong, GiaNhap)
    VALUES
        (N'PN001', N'SP001-42-DEN', 25, 2000000),
        (N'PN001', N'SP007-41-XANH', 15, 2500000),
        (N'PN001', N'SP015-43-DO', 10, 3000000),
        (N'PN001', N'SP020-42-DEN', 15, 2800000),

        (N'PN002', N'SP002-41-TRANG', 30, 3000000),
        (N'PN002', N'SP008-40-TRANG', 45, 1200000),
        (N'PN002', N'SP011-38-HONG', 22, 1800000),
        (N'PN002', N'SP012-42-XAM', 30, 1500000),

        (N'PN003', N'SP005-39-TRANG', 20, 1600000),
        (N'PN003', N'SP013-40-TRANG', 15, 1400000),
        (N'PN003', N'SP014-41-DEN', 25, 1900000),
        (N'PN003', N'SP016-39-TRANG', 20, 1200000),

        (N'PN004', N'SP003-40-DEN', 50, 800000),
        (N'PN004', N'SP004-43-TRANG', 40, 1100000),
        (N'PN004', N'SP018-40-NAU', 15, 900000),
        (N'PN004', N'SP019-41-XANH', 20, 1300000),

        (N'PN005', N'SP006-42-DEN', 60, 600000),
        (N'PN005', N'SP009-42-DEN', 10, 900000),
        (N'PN005', N'SP010-41-NAU', 18, 700000),
        (N'PN005', N'SP017-42-DEN', 12, 850000);

    -- ==============================================================================
    -- 8. HÓA ĐƠN BÁN HÀNG (20 Hóa đơn thực tế, tính nhẩm sẵn Giá Bán = Giá Nhập * 1.2)
    -- ==============================================================================
    INSERT INTO dbo.HOA_DON (MaHD, MaNV, MaKH, MaVoucher, NgayLap, TongTien)
    VALUES
        (N'HD001', N'TNV002', N'KH001', N'WELCOME20', '2026-03-01 09:15:00', 1920000), 
        (N'HD002', N'TNV002', N'KH002', NULL, '2026-03-02 10:30:00', 3600000), 
        (N'HD003', N'TNV001', N'KH003', N'GIAM50K', '2026-03-03 14:45:00', 910000), 
        (N'HD004', N'TNV002', N'KH004', NULL, '2026-03-04 11:20:00', 1320000), 
        (N'HD005', N'TNV002', N'KH005', N'WELCOME20', '2026-03-05 16:10:00', 1620000), 
        (N'HD006', N'TNV001', N'KH006', NULL, '2026-03-06 08:30:00', 720000), 
        (N'HD007', N'TNV002', N'KH007', NULL, '2026-03-07 19:45:00', 3000000), 
        (N'HD008', N'TNV002', N'KH008', N'GIAM50K', '2026-03-08 20:00:00', 1390000), 
        (N'HD009', N'TNV001', N'KH009', NULL, '2026-03-09 13:15:00', 1170000), 
        (N'HD010', N'TNV002', N'KH010', N'WELCOME20', '2026-03-10 15:50:00', 728000), 
        (N'HD011', N'TNV002', N'KH011', NULL, '2026-03-11 11:11:00', 2160000), 
        (N'HD012', N'TNV001', N'KH012', NULL, '2026-03-12 14:22:00', 1800000), 
        (N'HD013', N'TNV002', N'KH013', N'GIAM50K', '2026-03-13 18:30:00', 1630000), 
        (N'HD014', N'TNV002', N'KH014', NULL, '2026-03-14 09:05:00', 2280000), 
        (N'HD015', N'TNV001', N'KH015', N'WELCOME20', '2026-03-15 10:45:00', 2880000), 
        (N'HD016', N'TNV002', N'KH016', NULL, '2026-03-16 12:15:00', 1440000), 
        (N'HD017', N'TNV002', N'KH017', NULL, '2026-03-17 17:50:00', 1105000), 
        (N'HD018', N'TNV001', N'KH018', N'GIAM50K', '2026-03-18 20:20:00', 1120000), 
        (N'HD019', N'TNV002', N'KH019', NULL, '2026-03-19 14:10:00', 1560000), 
        (N'HD020', N'TNV002', N'KH020', N'WELCOME20', '2026-03-20 16:30:00', 2688000);

    INSERT INTO dbo.CHI_TIET_HOA_DON (MaHD, MaSP, SoLuong, DonGia)
    VALUES
        (N'HD001', N'SP001-42-DEN', 1, 2400000),
        (N'HD002', N'SP002-41-TRANG', 1, 3600000),
        (N'HD003', N'SP003-40-DEN', 1, 960000),
        (N'HD004', N'SP004-43-TRANG', 1, 1320000),
        (N'HD005', N'SP005-39-TRANG', 1, 1920000),
        (N'HD006', N'SP006-42-DEN', 1, 720000),
        (N'HD007', N'SP007-41-XANH', 1, 3000000),
        (N'HD008', N'SP008-40-TRANG', 1, 1440000),
        (N'HD009', N'SP009-42-DEN', 1, 1170000),
        (N'HD010', N'SP010-41-NAU', 1, 910000),
        (N'HD011', N'SP011-38-HONG', 1, 2160000),
        (N'HD012', N'SP012-42-XAM', 1, 1800000),
        (N'HD013', N'SP013-40-TRANG', 1, 1680000),
        (N'HD014', N'SP014-41-DEN', 1, 2280000),
        (N'HD015', N'SP015-43-DO', 1, 3600000),
        (N'HD016', N'SP016-39-TRANG', 1, 1440000),
        (N'HD017', N'SP017-42-DEN', 1, 1105000),
        (N'HD018', N'SP018-40-NAU', 1, 1170000),
        (N'HD019', N'SP019-41-XANH', 1, 1560000),
        (N'HD020', N'SP020-42-DEN', 1, 3360000);

    -- ==============================================================================
    -- 9. PHIẾU ĐỔI TRẢ & BẢO HÀNH (Vài phiếu minh họa dựa trên hóa đơn trên)
    -- ==============================================================================
    INSERT INTO dbo.DOI_TRA (MaDT, MaHD, MaNV, MaSP, NgayDoiTra, SoLuong, TongTienHoan, LyDo, TinhTrang)
    VALUES
        (N'DT001', N'HD001', N'TNV002', N'SP001-42-DEN', '2026-03-05 10:00:00', 1, 2400000, N'Khách mang không vừa form', N'Đã hoàn tiền'),
        (N'DT002', N'HD003', N'TNV001', N'SP003-40-DEN', '2026-03-08 11:30:00', 1, 960000, N'Sản phẩm bị lỗi keo viền', N'Đã đổi sản phẩm');

    INSERT INTO dbo.PHIEU_BAO_HANH (MaPBH, MaHD, MaSP, MaKH, NgayNhan, NgayTraDuKien, LoiCanBaoHanh, ChiPhiPhatSinh, TrangThai)
    VALUES
        (N'BH001', N'HD002', N'SP002-41-TRANG', N'KH002', '2026-03-20 09:00:00', '2026-03-27 09:00:00', N'Lưới primeknit bị xổ chỉ', 0, 1),
        (N'BH002', N'HD014', N'SP014-41-DEN', N'KH014', '2026-03-25 10:00:00', '2026-04-01 10:00:00', N'Bong logo in nhiệt', 0, 0);

    -- ==============================================================================
    -- 10. TRẢ HÀNG NHÀ CUNG CẤP
    -- ==============================================================================
    INSERT INTO dbo.PHIEU_TRA_NCC (MaPT, MaPN, MaNV, MaNCC, NgayTao, TongSoMatHang, TongTien, LyDo, TrangThai, NgayXuLy, NguoiXuLy)
    VALUES
        (N'PT001', N'PN004', N'TNV003', N'NCC004', '2026-02-05', 1, 800000, N'Lô hàng Vans bị móp hộp diện rộng', N'Đã duyệt', '2026-02-06', N'TNV002');

    INSERT INTO dbo.CHI_TIET_TRA_NCC (MaPT, MaSP, SoLuong, GiaNhap)
    VALUES
        (N'PT001', N'SP003-40-DEN', 5, 800000);

    -- ==============================================================================
    -- 11. CẬP NHẬT TỰ ĐỘNG GIÁ BÁN THEO CÔNG THỨC: GIÁ NHẬP + (GIÁ NHẬP * % LỢI NHUẬN)
    -- ==============================================================================
    UPDATE sp
    SET sp.DonGia = ROUND(ct.GiaNhap * (1 + sp.PhanTramLoiNhuan / 100.0), 2)
    FROM dbo.SAN_PHAM sp
    CROSS APPLY (
        SELECT TOP (1) ct.GiaNhap
        FROM dbo.CHI_TIET_PHIEU_NHAP ct
        INNER JOIN dbo.PHIEU_NHAP pn ON pn.MaPN = ct.MaPN
        WHERE ct.MaSP = sp.MaSP
        ORDER BY pn.NgayNhap ASC, pn.MaPN ASC
    ) ct

    COMMIT TRANSACTION
    PRINT N'Thêm dữ liệu mẫu thực tế thành công!';
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION
    THROW
END CATCH
GO