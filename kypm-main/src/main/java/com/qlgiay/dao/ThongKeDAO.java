package com.qlgiay.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.qlgiay.dto.DoanhThuTheoNgayDTO;
import com.qlgiay.dto.LoaiSanPhamThongKeDTO;
import com.qlgiay.dto.LoiNhuanTheoKyDTO;
import com.qlgiay.dto.TopKhachHangDTO;
import com.qlgiay.dto.TopSanPhamDTO;
import com.qlgiay.util.DBConnect;

public class ThongKeDAO {

    public BigDecimal getTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT COALESCE(SUM(TongTien), 0)
                FROM HOA_DON
                WHERE NgayLap >= ? AND NgayLap < DATEADD(day, 1, ?)
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal(1);
                    return value != null ? value : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getTongTienVon(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT COALESCE(SUM(TongTien), 0)
                FROM PHIEU_NHAP
                WHERE NgayNhap >= ? AND NgayNhap < DATEADD(day, 1, ?)
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal(1);
                    return value != null ? value : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public int getSoHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT COUNT(*)
                FROM HOA_DON
                WHERE NgayLap BETWEEN ? AND ?
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTongSanPhamBan(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT COALESCE(SUM(ct.SoLuong), 0)
                FROM HOA_DON hd
                JOIN CHI_TIET_HOA_DON ct ON hd.MaHD = ct.MaHD
                WHERE hd.NgayLap BETWEEN ? AND ?
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getSoKhachMua(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT COUNT(DISTINCT MaKH)
                FROM HOA_DON
                WHERE NgayLap BETWEEN ? AND ?
                  AND MaKH IS NOT NULL
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<DoanhThuTheoNgayDTO> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<DoanhThuTheoNgayDTO> list = new ArrayList<>();

        String sql = """
                SELECT CAST(NgayLap AS DATE) AS Ngay, COALESCE(SUM(TongTien), 0) AS DoanhThu
                FROM HOA_DON
                WHERE NgayLap BETWEEN ? AND ?
                GROUP BY CAST(NgayLap AS DATE)
                ORDER BY Ngay
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DoanhThuTheoNgayDTO dto = new DoanhThuTheoNgayDTO();
                    dto.setNgay(rs.getDate("Ngay").toLocalDate());
                    dto.setDoanhThu(rs.getBigDecimal("DoanhThu"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TopSanPhamDTO> getTopSanPham(LocalDate tuNgay, LocalDate denNgay) {
        List<TopSanPhamDTO> list = new ArrayList<>();

        String sql = """
                SELECT TOP 5
                    sp.MaSP,
                    sp.TenSP,
                    SUM(ct.SoLuong) AS SoLuongBan,
                    SUM(ct.SoLuong * ct.DonGia) AS DoanhThu
                FROM HOA_DON hd
                JOIN CHI_TIET_HOA_DON ct ON hd.MaHD = ct.MaHD
                JOIN SAN_PHAM sp ON ct.MaSP = sp.MaSP
                WHERE hd.NgayLap BETWEEN ? AND ?
                GROUP BY sp.MaSP, sp.TenSP
                ORDER BY SoLuongBan DESC, DoanhThu DESC
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopSanPhamDTO dto = new TopSanPhamDTO();
                    dto.setMaSP(rs.getString("MaSP"));
                    dto.setTenSP(rs.getString("TenSP"));
                    dto.setSoLuongBan(rs.getInt("SoLuongBan"));
                    dto.setDoanhThu(rs.getBigDecimal("DoanhThu"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TopKhachHangDTO> getTopKhachHang(LocalDate tuNgay, LocalDate denNgay) {
        List<TopKhachHangDTO> list = new ArrayList<>();

        String sql = """
                SELECT TOP 10
                    kh.MaKH,
                    kh.TenKH,
                    COUNT(hd.MaHD) AS SoHoaDon,
                    SUM(hd.TongTien) AS TongChiTieu
                FROM HOA_DON hd
                JOIN KHACH_HANG kh ON hd.MaKH = kh.MaKH
                WHERE hd.NgayLap BETWEEN ? AND ?
                GROUP BY kh.MaKH, kh.TenKH
                ORDER BY TongChiTieu DESC, SoHoaDon DESC
                """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopKhachHangDTO dto = new TopKhachHangDTO();
                    dto.setMaKH(rs.getString("MaKH"));
                    dto.setTenKH(rs.getString("TenKH"));
                    dto.setSoHoaDon(rs.getInt("SoHoaDon"));
                    dto.setTongChiTieu(rs.getBigDecimal("TongChiTieu"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<LoaiSanPhamThongKeDTO> getThongKeTheoLoai(LocalDate tuNgay, LocalDate denNgay) {
        List<LoaiSanPhamThongKeDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                sp.LoaiSP,
                SUM(ct.SoLuong) AS SoLuongBan
            FROM HOA_DON hd
            JOIN CHI_TIET_HOA_DON ct ON hd.MaHD = ct.MaHD
            JOIN SAN_PHAM sp ON ct.MaSP = sp.MaSP
            WHERE hd.NgayLap BETWEEN ? AND ?
            GROUP BY sp.LoaiSP
            ORDER BY SoLuongBan DESC
            """;

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiSanPhamThongKeDTO dto = new LoaiSanPhamThongKeDTO();
                    dto.setLoaiSP(rs.getString("LoaiSP"));
                    dto.setSoLuongBan(rs.getInt("SoLuongBan"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<LoiNhuanTheoKyDTO> getLoiNhuanTheoThang(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT Nam, Ky, SUM(DoanhThu) AS DoanhThu, SUM(TienVon) AS TienVon
                FROM (
                    SELECT YEAR(NgayLap) AS Nam, MONTH(NgayLap) AS Ky,
                           TongTien AS DoanhThu, CAST(0 AS decimal(18, 2)) AS TienVon
                    FROM HOA_DON
                    WHERE NgayLap >= ? AND NgayLap < DATEADD(day, 1, ?)
                    UNION ALL
                    SELECT YEAR(NgayNhap), MONTH(NgayNhap),
                           CAST(0 AS decimal(18, 2)), TongTien
                    FROM PHIEU_NHAP
                    WHERE NgayNhap >= ? AND NgayNhap < DATEADD(day, 1, ?)
                ) AS TongHop
                GROUP BY Nam, Ky
                ORDER BY Nam, Ky
                """;
        return getLoiNhuanTheoKy(sql, tuNgay, denNgay);
    }

    public List<LoiNhuanTheoKyDTO> getLoiNhuanTheoQuy(LocalDate tuNgay, LocalDate denNgay) {
        String sql = """
                SELECT Nam, Ky, SUM(DoanhThu) AS DoanhThu, SUM(TienVon) AS TienVon
                FROM (
                    SELECT YEAR(NgayLap) AS Nam, DATEPART(QUARTER, NgayLap) AS Ky,
                           TongTien AS DoanhThu, CAST(0 AS decimal(18, 2)) AS TienVon
                    FROM HOA_DON
                    WHERE NgayLap >= ? AND NgayLap < DATEADD(day, 1, ?)
                    UNION ALL
                    SELECT YEAR(NgayNhap), DATEPART(QUARTER, NgayNhap),
                           CAST(0 AS decimal(18, 2)), TongTien
                    FROM PHIEU_NHAP
                    WHERE NgayNhap >= ? AND NgayNhap < DATEADD(day, 1, ?)
                ) AS TongHop
                GROUP BY Nam, Ky
                ORDER BY Nam, Ky
                """;
        return getLoiNhuanTheoKy(sql, tuNgay, denNgay);
    }

    private List<LoiNhuanTheoKyDTO> getLoiNhuanTheoKy(String sql, LocalDate tuNgay, LocalDate denNgay) {
        List<LoiNhuanTheoKyDTO> list = new ArrayList<>();

        try (
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ps.setDate(3, Date.valueOf(tuNgay));
            ps.setDate(4, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoiNhuanTheoKyDTO dto = new LoiNhuanTheoKyDTO();
                    dto.setNam(rs.getInt("Nam"));
                    dto.setKy(rs.getInt("Ky"));
                    dto.setDoanhThu(rs.getBigDecimal("DoanhThu"));
                    dto.setTienVon(rs.getBigDecimal("TienVon"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}