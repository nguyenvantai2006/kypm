package com.qlgiay.dao;

import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {
    private static final String SELECT_ALL = """
            SELECT MaNCC, TenNCC, SDT, DiaChi, TrangThai
            FROM NHA_CUNG_CAP
            """;

    public List<NhaCungCapDTO> findAllActive() {
        String sql = SELECT_ALL + " WHERE TrangThai = 1 ORDER BY MaNCC";
        List<NhaCungCapDTO> list = new ArrayList<>();

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

    public List<NhaCungCapDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY MaNCC";
        List<NhaCungCapDTO> list = new ArrayList<>();

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

    public NhaCungCapDTO findById(String maNCC) {
        String sql = SELECT_ALL + " WHERE MaNCC = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maNCC);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public NhaCungCapDTO findByIdActive(String maNCC) {
        String sql = SELECT_ALL + " WHERE MaNCC = ? AND TrangThai = 1";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maNCC);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<NhaCungCapDTO> searchActive(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaNCC LIKE ? OR TenNCC LIKE ? OR SDT LIKE ?)
                  AND TrangThai = 1
                ORDER BY MaNCC
                """;
        List<NhaCungCapDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<NhaCungCapDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaNCC LIKE ? OR TenNCC LIKE ? OR SDT LIKE ?)
                ORDER BY MaNCC
                """;
        List<NhaCungCapDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(NhaCungCapDTO ncc) {
        String sql = """
                INSERT INTO NHA_CUNG_CAP (MaNCC, TenNCC, SDT, DiaChi, TrangThai)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ncc.getMaNCC());
            ps.setString(2, ncc.getTenNCC());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getDiaChi());
            ps.setInt(5, ncc.getTrangThai() == 0 ? 1 : ncc.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhaCungCapDTO ncc) {
        String sql = """
                UPDATE NHA_CUNG_CAP SET
                    TenNCC=?, SDT=?, DiaChi=?, TrangThai=?
                WHERE MaNCC=?
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getSdt());
            ps.setString(3, ncc.getDiaChi());
            ps.setInt(4, ncc.getTrangThai());
            ps.setString(5, ncc.getMaNCC());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(String maNCC) {
        String sql = "UPDATE NHA_CUNG_CAP SET TrangThai = 0 WHERE MaNCC = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restore(String maNCC) {
        String sql = "UPDATE NHA_CUNG_CAP SET TrangThai = 1 WHERE MaNCC = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private NhaCungCapDTO mapRow(ResultSet rs) throws SQLException {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNCC(rs.getString("MaNCC"));
        ncc.setTenNCC(rs.getString("TenNCC"));
        ncc.setSdt(rs.getString("SDT"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setTrangThai(rs.getInt("TrangThai"));
        return ncc;
    }
}