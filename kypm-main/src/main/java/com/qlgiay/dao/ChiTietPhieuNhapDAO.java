package com.qlgiay.dao;

import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhapDAO {
    private static final String SELECT_ALL = """
            SELECT MaPN, MaSP, SoLuong, GiaNhap
            FROM CHI_TIET_PHIEU_NHAP
            """;

    public List<ChiTietPhieuNhapDTO> findByMaPN(String maPN) {
        String sql = SELECT_ALL + " WHERE MaPN = ? ORDER BY MaSP";
        List<ChiTietPhieuNhapDTO> list = new ArrayList<>();

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maPN);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(ChiTietPhieuNhapDTO ct) {
        try (Connection c = DBConnect.getConnection()) {
            return insert(c, ct);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Connection c, ChiTietPhieuNhapDTO ct) {
        String sql = """
                INSERT INTO CHI_TIET_PHIEU_NHAP (MaPN, MaSP, SoLuong, GiaNhap)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ct.getMaPN());
            ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuong());
            ps.setBigDecimal(4, ct.getGiaNhap());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertList(Connection c, List<ChiTietPhieuNhapDTO> list) {
        if (list == null || list.isEmpty()) return true;

        for (ChiTietPhieuNhapDTO ct : list) {
            if (!insert(c, ct)) return false;
        }

        return true;
    }

    private ChiTietPhieuNhapDTO mapRow(ResultSet rs) throws SQLException {
        ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();

        ct.setMaPN(rs.getString("MaPN"));
        ct.setMaSP(rs.getString("MaSP"));
        ct.setSoLuong(rs.getInt("SoLuong"));
        ct.setGiaNhap(rs.getBigDecimal("GiaNhap"));

        return ct;
    }
}