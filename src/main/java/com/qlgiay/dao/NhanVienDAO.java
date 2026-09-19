package com.qlgiay.dao;

import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private static final String SELECT_ALL = """
            SELECT MaNV, Ho, Ten, MaQuyen, TaiKhoan, MatKhau, Luong, TrangThai
            FROM NHAN_VIEN
            """;

    public List<NhanVienDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY MaNV";
        List<NhanVienDTO> list = new ArrayList<>();

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

    public List<NhanVienDTO> findAllActive() {
        String sql = SELECT_ALL + " WHERE TrangThai = 1 ORDER BY MaNV";
        List<NhanVienDTO> list = new ArrayList<>();

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

    public NhanVienDTO findById(String maNV) {
        String sql = SELECT_ALL + " WHERE MaNV = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maNV);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public NhanVienDTO findByTaiKhoan(String taiKhoan) {
        String sql = SELECT_ALL + " WHERE TaiKhoan = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, taiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insert(NhanVienDTO nv) {
        String sql = """
                INSERT INTO NHAN_VIEN (MaNV, Ho, Ten, MaQuyen, TaiKhoan, MatKhau, Luong, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHo());
            ps.setString(3, nv.getTen());
            ps.setString(4, nv.getMaQuyen());
            ps.setString(5, nv.getTaiKhoan());
            ps.setString(6, nv.getMatKhau());

            if (nv.getLuong() != null) ps.setBigDecimal(7, nv.getLuong());
            else ps.setNull(7, Types.DECIMAL);

            ps.setInt(8, nv.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhanVienDTO nv) {
        boolean changePassword = nv.getMatKhau() != null;

        String sql;
        if (changePassword) {
            sql = """
                    UPDATE NHAN_VIEN SET
                        Ho = ?,
                        Ten = ?,
                        MaQuyen = ?,
                        TaiKhoan = ?,
                        MatKhau = ?,
                        Luong = ?,
                        TrangThai = ?
                    WHERE MaNV = ?
                    """;
        } else {
            sql = """
                    UPDATE NHAN_VIEN SET
                        Ho = ?,
                        Ten = ?,
                        MaQuyen = ?,
                        TaiKhoan = ?,
                        Luong = ?,
                        TrangThai = ?
                    WHERE MaNV = ?
                    """;
        }

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nv.getHo());
            ps.setString(2, nv.getTen());
            ps.setString(3, nv.getMaQuyen());
            ps.setString(4, nv.getTaiKhoan());

            int idx = 5;

            if (changePassword) {
                ps.setString(idx++, nv.getMatKhau());
            }

            if (nv.getLuong() != null) ps.setBigDecimal(idx++, nv.getLuong());
            else ps.setNull(idx++, Types.DECIMAL);

            ps.setInt(idx++, nv.getTrangThai());
            ps.setString(idx, nv.getMaNV());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(String maNV, int trangThai) {
        String sql = "UPDATE NHAN_VIEN SET TrangThai = ? WHERE MaNV = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, trangThai);
            ps.setString(2, maNV);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<NhanVienDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaNV LIKE ? OR Ho LIKE ? OR Ten LIKE ? OR TaiKhoan LIKE ? OR MaQuyen LIKE ?)
                ORDER BY MaNV
                """;

        List<NhanVienDTO> list = new ArrayList<>();
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

    public List<NhanVienDTO> searchActive(String keyword) {
        String sql = SELECT_ALL + """
                WHERE TrangThai = 1
                  AND (MaNV LIKE ? OR Ho LIKE ? OR Ten LIKE ? OR TaiKhoan LIKE ? OR MaQuyen LIKE ?)
                ORDER BY MaNV
                """;

        List<NhanVienDTO> list = new ArrayList<>();
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

    private NhanVienDTO mapRow(ResultSet rs) throws SQLException {
        return new NhanVienDTO(
                rs.getString("MaNV"),
                rs.getString("Ho"),
                rs.getString("Ten"),
                rs.getString("MaQuyen"),
                rs.getString("TaiKhoan"),
                rs.getString("MatKhau"),
                rs.getBigDecimal("Luong"),
                rs.getInt("TrangThai")
        );
    }
}