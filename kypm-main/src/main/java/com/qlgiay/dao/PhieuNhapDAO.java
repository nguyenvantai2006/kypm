package com.qlgiay.dao;

import com.qlgiay.dto.PhieuNhapDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDAO {
    private static final String SELECT_ALL = """
            SELECT MaPN, MaNV, MaNCC, NgayNhap, TongSoMatHang, TongTien
            FROM PHIEU_NHAP
            """;

    public List<PhieuNhapDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY NgayNhap DESC, MaPN DESC";
        List<PhieuNhapDTO> list = new ArrayList<>();

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

    public PhieuNhapDTO findById(String maPN) {
        String sql = SELECT_ALL + " WHERE MaPN = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maPN);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PhieuNhapDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaPN LIKE ? OR MaNV LIKE ? OR MaNCC LIKE ?)
                ORDER BY NgayNhap DESC, MaPN DESC
                """;
        List<PhieuNhapDTO> list = new ArrayList<>();
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

    public boolean insert(PhieuNhapDTO pn) {
        try (Connection c = DBConnect.getConnection()) {
            return insert(c, pn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Connection c, PhieuNhapDTO pn) {
        String sql = """
                INSERT INTO PHIEU_NHAP (MaPN, MaNV, MaNCC, NgayNhap, TongSoMatHang, TongTien)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, pn.getMaPN());
            ps.setString(2, pn.getMaNV());
            ps.setString(3, pn.getMaNCC());

            if (pn.getNgayNhap() == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, Date.valueOf(pn.getNgayNhap()));

            ps.setInt(5, pn.getTongSoMatHang());

            if (pn.getTongTien() == null) ps.setBigDecimal(6, BigDecimal.ZERO);
            else ps.setBigDecimal(6, pn.getTongTien());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTongTien(Connection c, String maPN, BigDecimal tongTien) {
        String sql = "UPDATE PHIEU_NHAP SET TongTien = ? WHERE MaPN = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBigDecimal(1, tongTien == null ? BigDecimal.ZERO : tongTien);
            ps.setString(2, maPN);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PhieuNhapDTO mapRow(ResultSet rs) throws SQLException {
        PhieuNhapDTO pn = new PhieuNhapDTO();

        pn.setMaPN(rs.getString("MaPN"));
        pn.setMaNV(rs.getString("MaNV"));
        pn.setMaNCC(rs.getString("MaNCC"));

        Date d = rs.getDate("NgayNhap");
        pn.setNgayNhap(d == null ? null : d.toLocalDate());

        pn.setTongSoMatHang(rs.getInt("TongSoMatHang"));
        pn.setTongTien(rs.getBigDecimal("TongTien"));

        return pn;
    }
}