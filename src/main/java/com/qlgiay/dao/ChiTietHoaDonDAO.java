package com.qlgiay.dao;

import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    private static final String SELECT_ALL = """
            SELECT MaHD, MaSP, SoLuong, DonGia
            FROM CHI_TIET_HOA_DON
            """;

    public List<ChiTietHoaDonDTO> findByMaHD(String maHD) {
        String sql = SELECT_ALL + " WHERE MaHD = ? ORDER BY MaSP";
        List<ChiTietHoaDonDTO> list = new ArrayList<>();

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

    public ChiTietHoaDonDTO findById(Connection c, String maHD, String maSP) {
        String sql = SELECT_ALL + " WHERE MaHD = ? AND MaSP = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ps.setString(2, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insert(ChiTietHoaDonDTO ct) {
        try (Connection c = DBConnect.getConnection()) {
            return insert(c, ct);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Connection c, ChiTietHoaDonDTO ct) {
        String sql = """
                INSERT INTO CHI_TIET_HOA_DON (MaHD, MaSP, SoLuong, DonGia)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ct.getMaHD());
            ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuong());
            ps.setBigDecimal(4, ct.getDonGia());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertList(Connection c, List<ChiTietHoaDonDTO> list) {
        if (list == null || list.isEmpty()) return true;

        for (ChiTietHoaDonDTO ct : list) {
            if (!insert(c, ct)) return false;
        }

        return true;
    }

    private ChiTietHoaDonDTO mapRow(ResultSet rs) throws SQLException {
        ChiTietHoaDonDTO ct = new ChiTietHoaDonDTO();

        ct.setMaHD(rs.getString("MaHD"));
        ct.setMaSP(rs.getString("MaSP"));
        ct.setSoLuong(rs.getInt("SoLuong"));
        ct.setDonGia(rs.getBigDecimal("DonGia"));

        return ct;
    }
}