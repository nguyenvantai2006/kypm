package com.qlgiay.dao;

import com.qlgiay.dto.DoiTraDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoiTraDAO {
    private static final String SELECT_ALL = """
            SELECT MaDT, MaHD, MaNV, MaSP, NgayDoiTra, SoLuong, TongTienHoan, LyDo, TinhTrang
            FROM DOI_TRA
            """;

    public List<DoiTraDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY NgayDoiTra DESC, MaDT DESC";
        List<DoiTraDTO> list = new ArrayList<>();

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

    public DoiTraDTO findById(String maDT) {
        String sql = SELECT_ALL + " WHERE MaDT = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maDT);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DoiTraDTO> findByMaHD(String maHD) {
        String sql = SELECT_ALL + " WHERE MaHD = ? ORDER BY NgayDoiTra DESC, MaDT DESC";
        List<DoiTraDTO> list = new ArrayList<>();

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

    public List<DoiTraDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaDT LIKE ? OR MaHD LIKE ? OR MaNV LIKE ? OR MaSP LIKE ? OR LyDo LIKE ?)
                ORDER BY NgayDoiTra DESC, MaDT DESC
                """;
        List<DoiTraDTO> list = new ArrayList<>();
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

    public boolean insert(DoiTraDTO dt) {
        String sql = """
                INSERT INTO DOI_TRA
                (MaDT, MaHD, MaNV, MaSP, NgayDoiTra, SoLuong, TongTienHoan, LyDo, TinhTrang)
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, dt.getMaDT());
            ps.setString(2, dt.getMaHD());
            ps.setString(3, dt.getMaNV());
            ps.setString(4, dt.getMaSP());

            if (dt.getNgayDoiTra() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(dt.getNgayDoiTra()));

            ps.setInt(6, dt.getSoLuong());

            if (dt.getTongTienHoan() == null) ps.setBigDecimal(7, BigDecimal.ZERO);
            else ps.setBigDecimal(7, dt.getTongTienHoan());

            ps.setString(8, dt.getLyDo());
            ps.setString(9, dt.getTinhTrang());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Connection c, DoiTraDTO dt) {
        String sql = """
                INSERT INTO DOI_TRA
                (MaDT, MaHD, MaNV, MaSP, NgayDoiTra, SoLuong, TongTienHoan, LyDo, TinhTrang)
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, dt.getMaDT());
            ps.setString(2, dt.getMaHD());
            ps.setString(3, dt.getMaNV());
            ps.setString(4, dt.getMaSP());

            if (dt.getNgayDoiTra() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(dt.getNgayDoiTra()));

            ps.setInt(6, dt.getSoLuong());
            ps.setBigDecimal(7, dt.getTongTienHoan() == null ? BigDecimal.ZERO : dt.getTongTienHoan());
            ps.setString(8, dt.getLyDo());
            ps.setString(9, dt.getTinhTrang());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private DoiTraDTO mapRow(ResultSet rs) throws SQLException {
        DoiTraDTO dt = new DoiTraDTO();

        dt.setMaDT(rs.getString("MaDT"));
        dt.setMaHD(rs.getString("MaHD"));
        dt.setMaNV(rs.getString("MaNV"));
        dt.setMaSP(rs.getString("MaSP"));

        Date d = rs.getDate("NgayDoiTra");
        dt.setNgayDoiTra(d == null ? null : d.toLocalDate());

        dt.setSoLuong(rs.getInt("SoLuong"));
        dt.setTongTienHoan(rs.getBigDecimal("TongTienHoan"));
        dt.setLyDo(rs.getString("LyDo"));
        dt.setTinhTrang(rs.getString("TinhTrang"));

        return dt;
    }
}