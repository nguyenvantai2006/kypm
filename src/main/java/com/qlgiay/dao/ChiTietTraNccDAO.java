package com.qlgiay.dao;

import com.qlgiay.dto.ChiTietTraNccDTO;
import com.qlgiay.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietTraNccDAO {
    public List<ChiTietTraNccDTO> findByMaPT(String maPT) {
        List<ChiTietTraNccDTO> list = new ArrayList<>();
        String sql = "SELECT MaPT, MaSP, SoLuong, GiaNhap FROM CHI_TIET_TRA_NCC WHERE MaPT = ? ORDER BY MaSP";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maPT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietTraNccDTO ct = new ChiTietTraNccDTO();
                    ct.setMaPT(rs.getString("MaPT")); ct.setMaSP(rs.getString("MaSP"));
                    ct.setSoLuong(rs.getInt("SoLuong")); ct.setGiaNhap(rs.getBigDecimal("GiaNhap"));
                    list.add(ct);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Connection c, ChiTietTraNccDTO ct) {
        String sql = "INSERT INTO CHI_TIET_TRA_NCC (MaPT, MaSP, SoLuong, GiaNhap) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ct.getMaPT()); ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuong()); ps.setBigDecimal(4, ct.getGiaNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
