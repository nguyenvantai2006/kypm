/*
   Price-flow migration for SQL Server.
   Import price is owned by CHI_TIET_PHIEU_NHAP.
    Product sale price is derived from the first import price and profit rate.
*/

IF COL_LENGTH('dbo.SAN_PHAM', 'PhanTramLoiNhuan') IS NULL
BEGIN
    ALTER TABLE dbo.SAN_PHAM
        ADD PhanTramLoiNhuan decimal(5, 2) NULL;
END;
GO

UPDATE dbo.SAN_PHAM
SET PhanTramLoiNhuan = 20
WHERE PhanTramLoiNhuan IS NULL OR PhanTramLoiNhuan <= 0;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.default_constraints
    WHERE name = 'DF_SAN_PHAM_PhanTramLoiNhuan'
)
BEGIN
    ALTER TABLE dbo.SAN_PHAM
        ADD CONSTRAINT DF_SAN_PHAM_PhanTramLoiNhuan
        DEFAULT (20) FOR PhanTramLoiNhuan;
END;
GO

/* Backfill the cached sale price from the first import price. */
UPDATE sp
SET sp.DonGia = ROUND(ct.GiaNhap * (1 + sp.PhanTramLoiNhuan / 100.0), 2)
FROM dbo.SAN_PHAM sp
CROSS APPLY (
    SELECT TOP (1) ct.GiaNhap
    FROM dbo.CHI_TIET_PHIEU_NHAP ct
    INNER JOIN dbo.PHIEU_NHAP pn ON pn.MaPN = ct.MaPN
    WHERE ct.MaSP = sp.MaSP
    ORDER BY pn.NgayNhap ASC, pn.MaPN ASC
) ct;
GO

/* Read-only verification query using the first import price as the baseline. */
SELECT
    sp.MaSP,
    sp.TenSP,
    firstImport.GiaNhap AS GiaNhapBanDau,
    sp.PhanTramLoiNhuan,
    ROUND(firstImport.GiaNhap * (1 + sp.PhanTramLoiNhuan / 100.0), 2) AS GiaBanTheoGiaDau,
    sp.DonGia AS GiaBanDangLuu
FROM dbo.SAN_PHAM sp
OUTER APPLY (
    SELECT TOP (1) ct.GiaNhap
    FROM dbo.CHI_TIET_PHIEU_NHAP ct
    INNER JOIN dbo.PHIEU_NHAP pn ON pn.MaPN = ct.MaPN
    WHERE ct.MaSP = sp.MaSP
    ORDER BY pn.NgayNhap ASC, pn.MaPN ASC
) firstImport
ORDER BY sp.MaSP;
GO
