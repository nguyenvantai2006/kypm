USE [QuanLyCuaHangGiay];
GO

/*
    Du lieu mau cho toan bo nghiep vu.
    Tat ca ma deu dung tien to T de khong trung du lieu co san.
    Can chay database/migration_phieu_tra_ncc.sql truoc neu chua co 2 bang tra NCC.
*/
SET XACT_ABORT ON;
GO

IF OBJECT_ID(N'dbo.PHIEU_TRA_NCC', N'U') IS NULL
    THROW 51000, N'Chua co bang PHIEU_TRA_NCC. Hay chay migration_phieu_tra_ncc.sql truoc.', 1;
IF OBJECT_ID(N'dbo.CHI_TIET_TRA_NCC', N'U') IS NULL
    THROW 51001, N'Chua co bang CHI_TIET_TRA_NCC. Hay chay migration_phieu_tra_ncc.sql truoc.', 1;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    DELETE FROM dbo.CHI_TIET_TRA_NCC WHERE MaPT IN (N'TPT001', N'TPT002', N'TPT003');
    DELETE FROM dbo.PHIEU_TRA_NCC WHERE MaPT IN (N'TPT001', N'TPT002', N'TPT003');
    DELETE FROM dbo.DOI_TRA WHERE MaDT IN (N'TDT001', N'TDT002', N'TDT003');
    DELETE FROM dbo.PHIEU_BAO_HANH WHERE MaPBH IN (N'TBH001', N'TBH002', N'TBH003');
    DELETE FROM dbo.CHI_TIET_HOA_DON WHERE MaHD IN (N'THD001', N'THD002', N'THD003');
    DELETE FROM dbo.HOA_DON WHERE MaHD IN (N'THD001', N'THD002', N'THD003');
    DELETE FROM dbo.CHI_TIET_PHIEU_NHAP WHERE MaPN IN (N'TPN001', N'TPN002', N'TPN003');
    DELETE FROM dbo.PHIEU_NHAP WHERE MaPN IN (N'TPN001', N'TPN002', N'TPN003');
    DELETE FROM dbo.VOUCHER WHERE MaVoucher IN (N'TVC001', N'TVC002', N'TVC003');
    DELETE FROM dbo.SAN_PHAM WHERE MaSP IN (N'TSP001', N'TSP002', N'TSP003');
    DELETE FROM dbo.KHACH_HANG WHERE MaKH IN (N'TKH001', N'TKH002', N'TKH003');
    DELETE FROM dbo.NHA_CUNG_CAP WHERE MaNCC IN (N'TNCC001', N'TNCC002', N'TNCC003');
    DELETE FROM dbo.NHAN_VIEN WHERE MaNV IN (N'TNV001', N'TNV002', N'TNV003');
    DELETE FROM dbo.QUYEN WHERE MaQuyen IN (N'TQ001', N'TQ002', N'TQ003');

    INSERT INTO dbo.QUYEN (MaQuyen, TenQuyen, QL_BanHang, QL_KhachHang, QL_SanPham, QL_NhapHang, QL_NhanVien, QL_ThongKe)
    VALUES
        (N'TQ001', N'Quản trị mẫu', 1, 1, 1, 1, 1, 1),
        (N'TQ002', N'Quản lý mẫu', 1, 1, 1, 1, 0, 1),
        (N'TQ003', N'Nhân viên mẫu', 1, 1, 1, 0, 0, 0);

    INSERT INTO dbo.NHAN_VIEN (MaNV, Ho, Ten, MaQuyen, TaiKhoan, MatKhau, Luong, TrangThai)
    VALUES
        (N'TNV001', N'Nguyễn', N'Quản Trị', N'TQ001', N'test.admin', N'123456', 18000000, 1),
        (N'TNV002', N'Trần', N'Bán Hàng', N'TQ002', N'test.manager', N'123456', 12000000, 1),
        (N'TNV003', N'Lê', N'Kho Hàng', N'TQ003', N'test.staff', N'123456', 9000000, 1);

    INSERT INTO dbo.KHACH_HANG (MaKH, TenKH, SDT, DiaChi, DiemTichLuy, TrangThai)
    VALUES
        (N'TKH001', N'Nguyễn Minh Anh', N'0901000001', N'12 Lê Lợi, Quận 1, TP.HCM', 120, 1),
        (N'TKH002', N'Trần Gia Huy', N'0901000002', N'45 Nguyễn Trãi, Quận 5, TP.HCM', 350, 1),
        (N'TKH003', N'Phạm Thu Hà', N'0901000003', N'78 Võ Văn Tần, Quận 3, TP.HCM', 0, 1);

    INSERT INTO dbo.NHA_CUNG_CAP (MaNCC, TenNCC, SDT, DiaChi, TrangThai)
    VALUES
        (N'TNCC001', N'Công ty Giày Việt', N'0911000001', N'Khu công nghiệp Tân Bình, TP.HCM', 1),
        (N'TNCC002', N'Nhà phân phối Thể Thao ABC', N'0911000002', N'Quận 7, TP.HCM', 1),
        (N'TNCC003', N'Xưởng Giày Da Đông Nam', N'0911000003', N'Bình Dương', 1);

    INSERT INTO dbo.SAN_PHAM (MaSP, TenSP, LoaiSP, DonViTinh, SoLuong, DonGia, MauSac, Size, ChatLieu, ThuongHieu, NuocSanXuat, NgaySanXuat, MoTa, HinhAnh, TrangThai)
    VALUES
        (N'TSP001', N'Giày Sneaker Urban', N'Giày Sneaker', N'Đôi', 56, 850000, N'Trắng', N'42', N'Vải Mesh', N'Urban', N'Việt Nam', '2026-01-10', N'Mẫu thử bán hàng', N'', 1),
        (N'TSP002', N'Giày Chạy Bộ Speed', N'Giày Chạy Bộ', N'Đôi', 42, 1250000, N'Đen', N'41', N'Vải Knit', N'Speed', N'Việt Nam', '2026-01-12', N'Mẫu thử chạy bộ', N'', 1),
        (N'TSP003', N'Giày Tây Classic', N'Giày Tây', N'Đôi', 31, 1450000, N'Nâu', N'40', N'Da bò', N'Classic', N'Việt Nam', '2025-12-20', N'Mẫu thử công sở', N'', 1);

    INSERT INTO dbo.VOUCHER (MaVoucher, TenVoucher, PhanTramGiam, SoTienGiam, GiamToiDa, DieuKienApDung, NgayBatDau, NgayKetThuc, SoLuong, TrangThai)
    VALUES
        (N'TVC001', N'Giảm 10 phần trăm mẫu', 10, NULL, 150000, 500000, '2026-01-01', '2026-12-31', 100, 1),
        (N'TVC002', N'Giảm trực tiếp 50K mẫu', 0, 50000, NULL, 300000, '2026-01-01', '2026-12-31', 50, 1),
        (N'TVC003', N'Voucher hết hạn mẫu', 20, NULL, 200000, 500000, '2025-01-01', '2025-12-31', 0, 0);

    INSERT INTO dbo.PHIEU_NHAP (MaPN, MaNV, MaNCC, NgayNhap, TongSoMatHang, TongTien)
    VALUES
        (N'TPN001', N'TNV003', N'TNCC001', '2026-02-01', 10, 8300000),
        (N'TPN002', N'TNV003', N'TNCC002', '2026-02-10', 11, 9550000),
        (N'TPN003', N'TNV002', N'TNCC003', '2026-02-20', 9, 8250000);

    INSERT INTO dbo.CHI_TIET_PHIEU_NHAP (MaPN, MaSP, SoLuong, GiaNhap)
    VALUES
        (N'TPN001', N'TSP001', 5, 650000), (N'TPN001', N'TSP002', 3, 950000), (N'TPN001', N'TSP003', 2, 1100000),
        (N'TPN002', N'TSP001', 4, 650000), (N'TPN002', N'TSP002', 5, 950000), (N'TPN002', N'TSP003', 2, 1100000),
        (N'TPN003', N'TSP001', 3, 650000), (N'TPN003', N'TSP002', 2, 950000), (N'TPN003', N'TSP003', 4, 1100000);

    INSERT INTO dbo.HOA_DON (MaHD, MaNV, MaKH, MaVoucher, NgayLap, TongTien)
    VALUES
        (N'THD001', N'TNV002', N'TKH001', N'TVC001', '2026-03-01 09:15:00', 3195000),
        (N'THD002', N'TNV002', N'TKH002', N'TVC002', '2026-03-05 14:30:00', 3500000),
        (N'THD003', N'TNV001', N'TKH003', NULL, '2026-03-10 16:45:00', 3550000);

    INSERT INTO dbo.CHI_TIET_HOA_DON (MaHD, MaSP, SoLuong, DonGia)
    VALUES
        (N'THD001', N'TSP001', 1, 850000), (N'THD001', N'TSP002', 1, 1250000), (N'THD001', N'TSP003', 1, 1450000),
        (N'THD002', N'TSP001', 1, 850000), (N'THD002', N'TSP002', 1, 1250000), (N'THD002', N'TSP003', 1, 1450000);

    INSERT INTO dbo.CHI_TIET_HOA_DON (MaHD, MaSP, SoLuong, DonGia)
    VALUES
        (N'THD003', N'TSP001', 1, 850000), (N'THD003', N'TSP002', 1, 1250000), (N'THD003', N'TSP003', 1, 1450000);

    INSERT INTO dbo.DOI_TRA (MaDT, MaHD, MaNV, MaSP, NgayDoiTra, SoLuong, TongTienHoan, LyDo, TinhTrang)
    VALUES
        (N'TDT001', N'THD001', N'TNV002', N'TSP001', '2026-03-03 10:00:00', 1, 850000, N'Không vừa size', N'Đã nhận hàng'),
        (N'TDT002', N'THD002', N'TNV002', N'TSP002', '2026-03-07 11:30:00', 1, 1250000, N'Sản phẩm lỗi đường may', N'Đã đổi sản phẩm'),
        (N'TDT003', N'THD003', N'TNV001', N'TSP003', '2026-03-12 15:00:00', 1, 1450000, N'Khách đổi ý', N'Đã hoàn tiền');

    INSERT INTO dbo.PHIEU_BAO_HANH (MaPBH, MaHD, MaSP, MaKH, NgayNhan, NgayTraDuKien, LoiCanBaoHanh, ChiPhiPhatSinh, TrangThai)
    VALUES
        (N'TBH001', N'THD001', N'TSP002', N'TKH001', '2026-03-04 09:00:00', '2026-03-11 09:00:00', N'Keo đế bị bong', 0, 0),
        (N'TBH002', N'THD002', N'TSP001', N'TKH002', '2026-03-08 10:00:00', '2026-03-15 10:00:00', N'Khóa dây giày bị hỏng', 50000, 1),
        (N'TBH003', N'THD003', N'TSP003', N'TKH003', '2026-03-13 13:30:00', '2026-03-20 13:30:00', N'Đường chỉ bị bung', 0, 2);

    INSERT INTO dbo.PHIEU_TRA_NCC (MaPT, MaPN, MaNV, MaNCC, NgayTao, TongSoMatHang, TongTien, LyDo, TrangThai, NgayXuLy, NguoiXuLy)
    VALUES
        (N'TPT001', N'TPN001', N'TNV003', N'TNCC001', '2026-02-05', 1, 650000, N'Nhập dư số lượng', N'Đã duyệt', '2026-02-06', N'TNV002'),
        (N'TPT002', N'TPN002', N'TNV003', N'TNCC002', '2026-02-15', 1, 950000, N'Hàng lỗi', N'Đang duyệt', NULL, NULL),
        (N'TPT003', N'TPN003', N'TNV002', N'TNCC003', '2026-02-25', 1, 1100000, N'Sai mẫu sản phẩm', N'Từ chối', '2026-02-26', N'TNV001');

    INSERT INTO dbo.CHI_TIET_TRA_NCC (MaPT, MaSP, SoLuong, GiaNhap)
    VALUES
        (N'TPT001', N'TSP001', 1, 650000),
        (N'TPT002', N'TSP002', 1, 950000),
        (N'TPT003', N'TSP003', 1, 1100000);

    COMMIT TRANSACTION;
    PRINT N'Đã nạp thành công dữ liệu mẫu T cho toàn bộ nghiệp vụ.';
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO