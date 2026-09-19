package com.qlgiay.dao;

import com.qlgiay.dto.PhieuTraNccDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuTraNccDAO {
    private String lastError;
    private static final String SELECT_ALL = """
            SELECT MaPT, MaPN, MaNV, MaNCC, NgayTao, TongSoMatHang, TongTien,
                   LyDo, TrangThai, NgayXuLy, NguoiXuLy
            FROM PHIEU_TRA_NCC
            """;

    public List<PhieuTraNccDTO> findAll() {
        List<PhieuTraNccDTO> list = new ArrayList<>();
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL + " ORDER BY NgayTao DESC, MaPT DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public PhieuTraNccDTO findById(String maPT) {
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL + " WHERE MaPT = ?")) {
            ps.setString(1, maPT);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean insert(Connection c, PhieuTraNccDTO p) {
        String sql = """
                INSERT INTO PHIEU_TRA_NCC
                (MaPT, MaPN, MaNV, MaNCC, NgayTao, TongSoMatHang, TongTien, LyDo, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (p.getNgayTao() == null) p.setNgayTao(LocalDate.now());
            ps.setString(1, p.getMaPT()); ps.setString(2, p.getMaPN());
            ps.setString(3, p.getMaNV()); ps.setString(4, p.getMaNCC());
            ps.setDate(5, Date.valueOf(p.getNgayTao()));
            ps.setInt(6, p.getTongSoMatHang());
            ps.setBigDecimal(7, p.getTongTien() == null ? BigDecimal.ZERO : p.getTongTien());
            ps.setString(8, p.getLyDo()); ps.setString(9, p.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { lastError = e.getMessage(); e.printStackTrace(); return false; }
    }

    public boolean updateStatus(Connection c, String maPT, String trangThai, String nguoiXuLy) {
        String sql = "UPDATE PHIEU_TRA_NCC SET TrangThai = ?, NgayXuLy = ?, NguoiXuLy = ? WHERE MaPT = ? AND TrangThai = N'Đang duyệt'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trangThai); ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setString(3, nguoiXuLy); ps.setString(4, maPT);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { lastError = e.getMessage(); e.printStackTrace(); return false; }
    }

    public String getLastError() {
        return lastError;
    }

    private PhieuTraNccDTO mapRow(ResultSet rs) throws SQLException {
        PhieuTraNccDTO p = new PhieuTraNccDTO();
        p.setMaPT(rs.getString("MaPT")); p.setMaPN(rs.getString("MaPN"));
        p.setMaNV(rs.getString("MaNV")); p.setMaNCC(rs.getString("MaNCC"));
        Date ngayTao = rs.getDate("NgayTao");
        Date ngayXuLy = rs.getDate("NgayXuLy");
        p.setNgayTao(ngayTao == null ? null : ngayTao.toLocalDate());
        p.setNgayXuLy(ngayXuLy == null ? null : ngayXuLy.toLocalDate());
        p.setTongSoMatHang(rs.getInt("TongSoMatHang")); p.setTongTien(rs.getBigDecimal("TongTien"));
        p.setLyDo(rs.getString("LyDo")); p.setTrangThai(rs.getString("TrangThai"));
        p.setNguoiXuLy(rs.getString("NguoiXuLy"));
        return p;
    }
}
