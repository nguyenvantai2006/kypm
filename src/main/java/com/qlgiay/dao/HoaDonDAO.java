package com.qlgiay.dao;

import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private static final String SELECT_ALL = """
            SELECT MaHD, MaNV, MaKH, MaVoucher, NgayLap, TongTien
            FROM HOA_DON
            """;

    public List<HoaDonDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY NgayLap DESC, MaHD DESC";
        List<HoaDonDTO> list = new ArrayList<>();

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

    public HoaDonDTO findById(String maHD) {
        String sql = SELECT_ALL + " WHERE MaHD = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<HoaDonDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaHD LIKE ? OR MaNV LIKE ? OR MaKH LIKE ? OR MaVoucher LIKE ?)
                ORDER BY NgayLap DESC, MaHD DESC
                """;
        List<HoaDonDTO> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    public boolean insert(HoaDonDTO hd) {
        try (Connection c = DBConnect.getConnection()) {
            return insert(c, hd);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Connection c, HoaDonDTO hd) {
        String sql = """
                INSERT INTO HOA_DON (MaHD, MaNV, MaKH, MaVoucher, NgayLap, TongTien)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getMaNV());

            if (hd.getMaKH() == null || hd.getMaKH().isBlank()) ps.setNull(3, Types.NVARCHAR);
            else ps.setString(3, hd.getMaKH());

            if (hd.getMaVoucher() == null || hd.getMaVoucher().isBlank()) ps.setNull(4, Types.NVARCHAR);
            else ps.setString(4, hd.getMaVoucher());

            if (hd.getNgayLap() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(hd.getNgayLap()));

            if (hd.getTongTien() == null) ps.setBigDecimal(6, BigDecimal.ZERO);
            else ps.setBigDecimal(6, hd.getTongTien());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTongTien(Connection c, String maHD, BigDecimal tongTien) {
        String sql = "UPDATE HOA_DON SET TongTien = ? WHERE MaHD = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, tongTien == null ? BigDecimal.ZERO : tongTien);
            ps.setString(2, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private HoaDonDTO mapRow(ResultSet rs) throws SQLException {
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaHD(rs.getString("MaHD"));
        hd.setMaNV(rs.getString("MaNV"));
        hd.setMaKH(rs.getString("MaKH"));
        hd.setMaVoucher(rs.getString("MaVoucher"));

        Date d = rs.getDate("NgayLap");
        hd.setNgayLap(d == null ? null : d.toLocalDate());

        hd.setTongTien(rs.getBigDecimal("TongTien"));
        return hd;
    }
}