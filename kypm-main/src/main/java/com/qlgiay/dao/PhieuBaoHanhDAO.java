package com.qlgiay.dao;

import com.qlgiay.dto.PhieuBaoHanhDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuBaoHanhDAO {
    private static final String SELECT_ALL = """
            SELECT MaPBH, MaHD, MaSP, MaKH, NgayNhan, NgayTraDuKien, LoiCanBaoHanh, ChiPhiPhatSinh, TrangThai
            FROM PHIEU_BAO_HANH
            """;

    public List<PhieuBaoHanhDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY NgayNhan DESC, MaPBH DESC";
        List<PhieuBaoHanhDTO> list = new ArrayList<>();

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

    public List<PhieuBaoHanhDTO> findAllActive() {
        String sql = SELECT_ALL + " WHERE TrangThai <> 2 ORDER BY NgayNhan DESC, MaPBH DESC";
        List<PhieuBaoHanhDTO> list = new ArrayList<>();

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

    public PhieuBaoHanhDTO findById(String maPBH) {
        String sql = SELECT_ALL + " WHERE MaPBH = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maPBH);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PhieuBaoHanhDTO findById(Connection c, String maPBH) {
        String sql = SELECT_ALL + " WHERE MaPBH = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maPBH);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PhieuBaoHanhDTO> findByMaHD(String maHD) {
        String sql = SELECT_ALL + " WHERE MaHD = ? ORDER BY NgayNhan DESC, MaPBH DESC";
        List<PhieuBaoHanhDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<PhieuBaoHanhDTO> findByMaKH(String maKH) {
        String sql = SELECT_ALL + " WHERE MaKH = ? ORDER BY NgayNhan DESC, MaPBH DESC";
        List<PhieuBaoHanhDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<PhieuBaoHanhDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaPBH LIKE ? OR MaHD LIKE ? OR MaSP LIKE ? OR MaKH LIKE ? OR LoiCanBaoHanh LIKE ?)
                ORDER BY NgayNhan DESC, MaPBH DESC
                """;
        List<PhieuBaoHanhDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);
            ps.setString(5, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(PhieuBaoHanhDTO pbh) {
        String sql = """
                INSERT INTO PHIEU_BAO_HANH
                (MaPBH, MaHD, MaSP, MaKH, NgayNhan, NgayTraDuKien, LoiCanBaoHanh, ChiPhiPhatSinh, TrangThai)
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, pbh.getMaPBH());
            ps.setString(2, pbh.getMaHD());
            ps.setString(3, pbh.getMaSP());
            ps.setString(4, pbh.getMaKH());

            if (pbh.getNgayNhan() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(pbh.getNgayNhan()));

            if (pbh.getNgayTraDuKien() == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, Date.valueOf(pbh.getNgayTraDuKien()));

            ps.setString(7, pbh.getLoiCanBaoHanh());

            if (pbh.getChiPhiPhatSinh() == null) ps.setBigDecimal(8, BigDecimal.ZERO);
            else ps.setBigDecimal(8, pbh.getChiPhiPhatSinh());

            ps.setInt(9, pbh.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Connection c, PhieuBaoHanhDTO pbh) {
        String sql = """
            INSERT INTO PHIEU_BAO_HANH
            (MaPBH, MaHD, MaSP, MaKH, NgayNhan, NgayTraDuKien, LoiCanBaoHanh, ChiPhiPhatSinh, TrangThai)
            VALUES
            (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, pbh.getMaPBH());
            ps.setString(2, pbh.getMaHD());
            ps.setString(3, pbh.getMaSP());
            ps.setString(4, pbh.getMaKH());

            if (pbh.getNgayNhan() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(pbh.getNgayNhan()));

            if (pbh.getNgayTraDuKien() == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, Date.valueOf(pbh.getNgayTraDuKien()));

            ps.setString(7, pbh.getLoiCanBaoHanh());

            if (pbh.getChiPhiPhatSinh() == null) ps.setBigDecimal(8, BigDecimal.ZERO);
            else ps.setBigDecimal(8, pbh.getChiPhiPhatSinh());

            ps.setInt(9, pbh.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(PhieuBaoHanhDTO pbh) {
        String sql = """
                UPDATE PHIEU_BAO_HANH SET
                    MaHD=?, MaSP=?, MaKH=?, NgayNhan=?, NgayTraDuKien=?, LoiCanBaoHanh=?, ChiPhiPhatSinh=?, TrangThai=?
                WHERE MaPBH=?
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, pbh.getMaHD());
            ps.setString(2, pbh.getMaSP());
            ps.setString(3, pbh.getMaKH());

            if (pbh.getNgayNhan() == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, Date.valueOf(pbh.getNgayNhan()));

            if (pbh.getNgayTraDuKien() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(pbh.getNgayTraDuKien()));

            ps.setString(6, pbh.getLoiCanBaoHanh());

            if (pbh.getChiPhiPhatSinh() == null) ps.setBigDecimal(7, BigDecimal.ZERO);
            else ps.setBigDecimal(7, pbh.getChiPhiPhatSinh());

            ps.setInt(8, pbh.getTrangThai());
            ps.setString(9, pbh.getMaPBH());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Connection c, PhieuBaoHanhDTO pbh) {
        String sql = """
            UPDATE PHIEU_BAO_HANH SET
                MaHD=?, MaSP=?, MaKH=?, NgayNhan=?, NgayTraDuKien=?, LoiCanBaoHanh=?, ChiPhiPhatSinh=?, TrangThai=?
            WHERE MaPBH=?
            """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, pbh.getMaHD());
            ps.setString(2, pbh.getMaSP());
            ps.setString(3, pbh.getMaKH());

            if (pbh.getNgayNhan() == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, Date.valueOf(pbh.getNgayNhan()));

            if (pbh.getNgayTraDuKien() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(pbh.getNgayTraDuKien()));

            ps.setString(6, pbh.getLoiCanBaoHanh());

            if (pbh.getChiPhiPhatSinh() == null) ps.setBigDecimal(7, BigDecimal.ZERO);
            else ps.setBigDecimal(7, pbh.getChiPhiPhatSinh());

            ps.setInt(8, pbh.getTrangThai());
            ps.setString(9, pbh.getMaPBH());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(String maPBH, int trangThai) {
        String sql = "UPDATE PHIEU_BAO_HANH SET TrangThai = ? WHERE MaPBH = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, trangThai);
            ps.setString(2, maPBH);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(Connection c, String maPBH, int trangThai) {
        String sql = "UPDATE PHIEU_BAO_HANH SET TrangThai = ? WHERE MaPBH = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trangThai);
            ps.setString(2, maPBH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PhieuBaoHanhDTO mapRow(ResultSet rs) throws SQLException {
        PhieuBaoHanhDTO pbh = new PhieuBaoHanhDTO();

        pbh.setMaPBH(rs.getString("MaPBH"));
        pbh.setMaHD(rs.getString("MaHD"));
        pbh.setMaSP(rs.getString("MaSP"));
        pbh.setMaKH(rs.getString("MaKH"));

        Date d1 = rs.getDate("NgayNhan");
        pbh.setNgayNhan(d1 == null ? null : d1.toLocalDate());

        Date d2 = rs.getDate("NgayTraDuKien");
        pbh.setNgayTraDuKien(d2 == null ? null : d2.toLocalDate());

        pbh.setLoiCanBaoHanh(rs.getString("LoiCanBaoHanh"));
        pbh.setChiPhiPhatSinh(rs.getBigDecimal("ChiPhiPhatSinh"));
        pbh.setTrangThai(rs.getInt("TrangThai"));

        return pbh;
    }
}