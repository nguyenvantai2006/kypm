package com.qlgiay.dao;

import com.qlgiay.dto.QuyenDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuyenDAO {
    private static final String SELECT_ALL = """
            SELECT MaQuyen, TenQuyen,
                   QL_BanHang, QL_KhachHang, QL_SanPham,
                   QL_NhapHang, QL_NhanVien, QL_ThongKe
            FROM QUYEN
            """;

    public List<QuyenDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY MaQuyen";
        List<QuyenDTO> list = new ArrayList<>();

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

    public QuyenDTO findById(String maQuyen) {
        String sql = SELECT_ALL + " WHERE MaQuyen = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maQuyen);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<QuyenDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaQuyen LIKE ? OR TenQuyen LIKE ?)
                ORDER BY MaQuyen
                """;

        List<QuyenDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(QuyenDTO q) {
        String sql = """
                INSERT INTO QUYEN
                (MaQuyen, TenQuyen, QL_BanHang, QL_KhachHang, QL_SanPham, QL_NhapHang, QL_NhanVien, QL_ThongKe)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, q.getMaQuyen());
            ps.setString(2, q.getTenQuyen());
            ps.setInt(3, q.getQlBanHang());
            ps.setInt(4, q.getQlKhachHang());
            ps.setInt(5, q.getQlSanPham());
            ps.setInt(6, q.getQlNhapHang());
            ps.setInt(7, q.getQlNhanVien());
            ps.setInt(8, q.getQlThongKe());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(QuyenDTO q) {
        String sql = """
                UPDATE QUYEN SET
                    TenQuyen = ?,
                    QL_BanHang = ?,
                    QL_KhachHang = ?,
                    QL_SanPham = ?,
                    QL_NhapHang = ?,
                    QL_NhanVien = ?,
                    QL_ThongKe = ?
                WHERE MaQuyen = ?
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, q.getTenQuyen());
            ps.setInt(2, q.getQlBanHang());
            ps.setInt(3, q.getQlKhachHang());
            ps.setInt(4, q.getQlSanPham());
            ps.setInt(5, q.getQlNhapHang());
            ps.setInt(6, q.getQlNhanVien());
            ps.setInt(7, q.getQlThongKe());
            ps.setString(8, q.getMaQuyen());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maQuyen) {
        String sql = "DELETE FROM QUYEN WHERE MaQuyen = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maQuyen);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private QuyenDTO mapRow(ResultSet rs) throws SQLException {
        return new QuyenDTO(
                rs.getString("MaQuyen"),
                rs.getString("TenQuyen"),
                rs.getInt("QL_BanHang"),
                rs.getInt("QL_KhachHang"),
                rs.getInt("QL_SanPham"),
                rs.getInt("QL_NhapHang"),
                rs.getInt("QL_NhanVien"),
                rs.getInt("QL_ThongKe")
        );
    }
}