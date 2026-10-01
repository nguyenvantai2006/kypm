package com.qlgiay.dao;

import com.qlgiay.dto.LichSuDTO;
import com.qlgiay.util.DBConnect;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LichSuDAO {
    public boolean insert(LichSuDTO log) {
        String sql = """
                INSERT INTO LICH_SU_HOAT_DONG
                    (MaNV, PhanLoai, HanhDong, DoiTuong, ThoiGian, ChiTiet)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, log.getMaNV());
            ps.setString(2, log.getPhanLoai());
            ps.setString(3, log.getHanhDong());
            ps.setString(4, log.getDoiTuong());
            ps.setTimestamp(5, Timestamp.valueOf(
                    log.getThoiGian() == null ? LocalDateTime.now() : log.getThoiGian()));
            ps.setString(6, log.getChiTiet());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LichSuDTO> findByPhanLoaiAndDate(String phanLoai, LocalDate tuNgay, LocalDate denNgay) {
        List<LichSuDTO> list = new ArrayList<>();
        String sql = """
                SELECT MaLog, MaNV, PhanLoai, HanhDong, DoiTuong, ThoiGian, ChiTiet
                FROM LICH_SU_HOAT_DONG
                WHERE PhanLoai = ?
                  AND ThoiGian >= ?
                  AND ThoiGian < DATEADD(day, 1, ?)
                ORDER BY ThoiGian DESC, MaLog DESC
                """;

        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phanLoai);
            ps.setDate(2, Date.valueOf(tuNgay));
            ps.setDate(3, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private LichSuDTO mapRow(ResultSet rs) throws Exception {
        LichSuDTO log = new LichSuDTO();
        log.setMaLog(rs.getInt("MaLog"));
        log.setMaNV(rs.getString("MaNV"));
        log.setPhanLoai(rs.getString("PhanLoai"));
        log.setHanhDong(rs.getString("HanhDong"));
        log.setDoiTuong(rs.getString("DoiTuong"));
        Timestamp timestamp = rs.getTimestamp("ThoiGian");
        log.setThoiGian(timestamp == null ? null : timestamp.toLocalDateTime());
        log.setChiTiet(rs.getString("ChiTiet"));
        return log;
    }
}
