package com.qlgiay.dao;

import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    private static final String SELECT_ALL = """
            SELECT MaKH, TenKH, SDT, DiaChi, DiemTichLuy, TrangThai
            FROM KHACH_HANG
            """;

    public List<KhachHangDTO> findAllActive() {
        String sql = SELECT_ALL + " WHERE TrangThai = 1 ORDER BY MaKH";
        List<KhachHangDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<KhachHangDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY MaKH";
        List<KhachHangDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public KhachHangDTO findById(String maKH) {
        String sql = SELECT_ALL + " WHERE MaKH = ?";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KhachHangDTO findByIdActive(String maKH) {
        String sql = SELECT_ALL + " WHERE MaKH = ? AND TrangThai = 1";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KhachHangDTO findByPhone(String sdt) {
        String sql = SELECT_ALL + " WHERE SDT = ?";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, sdt);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<KhachHangDTO> searchActive(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaKH LIKE ? OR TenKH LIKE ? OR SDT LIKE ?)
                  AND TrangThai = 1
                ORDER BY MaKH
                """;
        List<KhachHangDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<KhachHangDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaKH LIKE ? OR TenKH LIKE ? OR SDT LIKE ?)
                ORDER BY MaKH
                """;
        List<KhachHangDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(KhachHangDTO kh) {
        String sql = """
                INSERT INTO KHACH_HANG (MaKH, TenKH, SDT, DiaChi, DiemTichLuy, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getDiaChi());
            ps.setInt(5, kh.getDiemTichLuy());
            ps.setInt(6, kh.getTrangThai() == 0 ? 1 : kh.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhachHangDTO kh) {
        String sql = """
                UPDATE KHACH_HANG SET
                    TenKH=?, SDT=?, DiaChi=?, DiemTichLuy=?, TrangThai=?
                WHERE MaKH=?
                """;

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChi());
            ps.setInt(4, kh.getDiemTichLuy());
            ps.setInt(5, kh.getTrangThai());
            ps.setString(6, kh.getMaKH());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(String maKH) {
        String sql = "UPDATE KHACH_HANG SET TrangThai = 0 WHERE MaKH = ?";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restore(String maKH) {
        String sql = "UPDATE KHACH_HANG SET TrangThai = 1 WHERE MaKH = ?";

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addPoints(Connection c, String maKH, int diemCong) {
        String sql = "UPDATE KHACH_HANG SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(0, diemCong));
            ps.setString(2, maKH);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usePoints(Connection c, String maKH, int diemTru) {
        String sql = """
                UPDATE KHACH_HANG
                SET DiemTichLuy = DiemTichLuy - ?
                WHERE MaKH = ? AND DiemTichLuy >= ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            int d = Math.max(0, diemTru);
            ps.setInt(1, d);
            ps.setString(2, maKH);
            ps.setInt(3, d);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getPoints(Connection c, String maKH) {
        String sql = "SELECT DiemTichLuy FROM KHACH_HANG WHERE MaKH = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return 0;
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private KhachHangDTO mapRow(ResultSet rs) throws SQLException {
        KhachHangDTO kh = new KhachHangDTO();
        kh.setMaKH(rs.getString("MaKH"));
        kh.setTenKH(rs.getString("TenKH"));
        kh.setSdt(rs.getString("SDT"));
        kh.setDiaChi(rs.getString("DiaChi"));
        kh.setDiemTichLuy(rs.getInt("DiemTichLuy"));
        kh.setTrangThai(rs.getInt("TrangThai"));
        return kh;
    }
}