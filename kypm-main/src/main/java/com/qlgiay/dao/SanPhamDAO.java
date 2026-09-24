package com.qlgiay.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.DBConnect;

public class SanPhamDAO {
    private static final String SELECT_ALL = """
                 SELECT MaSP, TenSP, LoaiSP, DonViTinh, SoLuong, DonGia, MauSac, Size,
                     ChatLieu, ThuongHieu, NuocSanXuat, NgaySanXuat, MoTa, HinhAnh, MaNCC, TrangThai,
                     PhanTramLoiNhuan,
                     (SELECT TOP 1 ct.GiaNhap
                      FROM CHI_TIET_PHIEU_NHAP ct
                      INNER JOIN PHIEU_NHAP pn ON pn.MaPN = ct.MaPN
                      WHERE ct.MaSP = SAN_PHAM.MaSP
                      ORDER BY pn.NgayNhap DESC, pn.MaPN DESC) AS GiaNhap
            FROM SAN_PHAM
            """;

    public List<SanPhamDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY TenSP";
        List<SanPhamDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<SanPhamDTO> findAllActive() {
        String sql = SELECT_ALL + " WHERE TrangThai = 1 ORDER BY TenSP";
        List<SanPhamDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<SanPhamDTO> findBySupplier(String maNCC) {
        String sql = SELECT_ALL + """
                WHERE SAN_PHAM.MaNCC = ?
                   OR EXISTS (
                       SELECT 1
                       FROM CHI_TIET_PHIEU_NHAP ct
                       INNER JOIN PHIEU_NHAP pn ON pn.MaPN = ct.MaPN
                       WHERE ct.MaSP = SAN_PHAM.MaSP AND pn.MaNCC = ?
                   )
                ORDER BY SAN_PHAM.TenSP
                """;
        List<SanPhamDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            ps.setString(2, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public SanPhamDTO findById(String maSP) {
        String sql = SELECT_ALL + " WHERE MaSP = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SanPhamDTO findByIdActive(String maSP) {
        String sql = SELECT_ALL + " WHERE MaSP = ? AND TrangThai = 1";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<SanPhamDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaSP LIKE ? OR TenSP LIKE ? OR LoaiSP LIKE ? OR ThuongHieu LIKE ?)
                ORDER BY TenSP
                """;
        List<SanPhamDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<SanPhamDTO> searchActive(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaSP LIKE ? OR TenSP LIKE ? OR LoaiSP LIKE ? OR ThuongHieu LIKE ?)
                  AND TrangThai = 1
                ORDER BY TenSP
                """;
        List<SanPhamDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(SanPhamDTO sp) {
        String sql = """
                INSERT INTO SAN_PHAM
                (MaSP, TenSP, LoaiSP, DonViTinh, SoLuong, DonGia, MauSac, Size, ChatLieu,
                 ThuongHieu, NuocSanXuat, NgaySanXuat, MoTa, HinhAnh, MaNCC, TrangThai, PhanTramLoiNhuan)
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setString(3, sp.getLoaiSP());
            ps.setString(4, sp.getDonViTinh());
            ps.setInt(5, sp.getSoLuong());

            if (sp.getDonGia() == null) ps.setNull(6, Types.DECIMAL);
            else ps.setBigDecimal(6, sp.getDonGia());

            ps.setString(7, sp.getMauSac());
            ps.setString(8, sp.getSize());
            ps.setString(9, sp.getChatLieu());
            ps.setString(10, sp.getThuongHieu());
            ps.setString(11, sp.getNuocSanXuat());

            if (sp.getNgaySanXuat() == null) ps.setNull(12, Types.DATE);
            else ps.setDate(12, Date.valueOf(sp.getNgaySanXuat()));

            ps.setString(13, sp.getMoTa());
            ps.setString(14, sp.getHinhAnh());
            ps.setString(15, sp.getMaNCC());
            ps.setInt(16, sp.getTrangThai() == 0 ? 1 : sp.getTrangThai());
            ps.setBigDecimal(17, sp.getPhanTramLoiNhuan());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(SanPhamDTO sp) {
        String sql = """
                UPDATE SAN_PHAM SET
                    TenSP=?, LoaiSP=?, DonViTinh=?, SoLuong=?, DonGia=?, MauSac=?, Size=?, ChatLieu=?,
                    ThuongHieu=?, NuocSanXuat=?, NgaySanXuat=?, MoTa=?, HinhAnh=?, MaNCC=?, TrangThai=?,
                    PhanTramLoiNhuan=?
                WHERE MaSP=?
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, sp.getTenSP());
            ps.setString(2, sp.getLoaiSP());
            ps.setString(3, sp.getDonViTinh());
            ps.setInt(4, sp.getSoLuong());

            if (sp.getDonGia() == null) ps.setNull(5, Types.DECIMAL);
            else ps.setBigDecimal(5, sp.getDonGia());

            ps.setString(6, sp.getMauSac());
            ps.setString(7, sp.getSize());
            ps.setString(8, sp.getChatLieu());
            ps.setString(9, sp.getThuongHieu());
            ps.setString(10, sp.getNuocSanXuat());

            if (sp.getNgaySanXuat() == null) ps.setNull(11, Types.DATE);
            else ps.setDate(11, Date.valueOf(sp.getNgaySanXuat()));

            ps.setString(12, sp.getMoTa());
            ps.setString(13, sp.getHinhAnh());
            ps.setString(14, sp.getMaNCC());
            ps.setInt(15, sp.getTrangThai());
            ps.setBigDecimal(16, sp.getPhanTramLoiNhuan());
            ps.setString(17, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(String maSP) {
        String sql = "UPDATE SAN_PHAM SET TrangThai = 0 WHERE MaSP = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restore(String maSP) {
        String sql = "UPDATE SAN_PHAM SET TrangThai = 1 WHERE MaSP = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decreaseStock(Connection c, String maSP, int soLuong) {
        String sql = """
                UPDATE SAN_PHAM
                SET SoLuong = SoLuong - ?
                WHERE MaSP = ? AND SoLuong >= ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            int sl = Math.max(0, soLuong);
            ps.setInt(1, sl);
            ps.setString(2, maSP);
            ps.setInt(3, sl);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean increaseStock(Connection c, String maSP, int soLuong) {
        String sql = """
                UPDATE SAN_PHAM
                SET SoLuong = SoLuong + ?
                WHERE MaSP = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(0, soLuong));
            ps.setString(2, maSP);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDerivedSalePrice(Connection c, String maSP, java.math.BigDecimal giaNhap) {
        String sql = """
                UPDATE SAN_PHAM
                SET DonGia = ROUND(? * (1 + ISNULL(PhanTramLoiNhuan, 20) / 100.0), 2)
                WHERE MaSP = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, giaNhap);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private SanPhamDTO mapRow(ResultSet rs) throws SQLException {
        SanPhamDTO sp = new SanPhamDTO();
        sp.setMaSP(rs.getString("MaSP"));
        sp.setTenSP(rs.getString("TenSP"));
        sp.setLoaiSP(rs.getString("LoaiSP"));
        sp.setDonViTinh(rs.getString("DonViTinh"));
        sp.setSoLuong(rs.getInt("SoLuong"));
        sp.setGiaNhap(rs.getBigDecimal("GiaNhap"));
        java.math.BigDecimal loiNhuan = rs.getBigDecimal("PhanTramLoiNhuan");
        sp.setPhanTramLoiNhuan(loiNhuan == null ? java.math.BigDecimal.valueOf(20) : loiNhuan);
        sp.setDonGia(SanPhamDTO.tinhGiaBan(sp.getGiaNhap(), sp.getPhanTramLoiNhuan()));
        sp.setMauSac(rs.getString("MauSac"));
        sp.setSize(rs.getString("Size"));
        sp.setChatLieu(rs.getString("ChatLieu"));
        sp.setThuongHieu(rs.getString("ThuongHieu"));
        sp.setNuocSanXuat(rs.getString("NuocSanXuat"));

        Date d = rs.getDate("NgaySanXuat");
        sp.setNgaySanXuat(d == null ? null : d.toLocalDate());

        sp.setMoTa(rs.getString("MoTa"));
        sp.setHinhAnh(rs.getString("HinhAnh"));
        sp.setMaNCC(rs.getString("MaNCC"));
        sp.setTrangThai(rs.getInt("TrangThai"));
        return sp;
    }
}