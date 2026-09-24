package com.qlgiay.dao;

import com.qlgiay.dto.VoucherDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {
    private static final String SELECT_ALL = """
            SELECT MaVoucher, TenVoucher, PhanTramGiam, SoTienGiam, GiamToiDa, DieuKienApDung,
                   NgayBatDau, NgayKetThuc, SoLuong, TrangThai
            FROM VOUCHER
            """;

    public List<VoucherDTO> findAllActive() {
        String sql = SELECT_ALL + " WHERE TrangThai = 1 ORDER BY TenVoucher";
        List<VoucherDTO> list = new ArrayList<>();

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

    public List<VoucherDTO> findAll() {
        String sql = SELECT_ALL + " ORDER BY TenVoucher";
        List<VoucherDTO> list = new ArrayList<>();

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

    public VoucherDTO findById(String maVoucher) {
        String sql = SELECT_ALL + " WHERE MaVoucher = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maVoucher);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VoucherDTO findByIdActive(String maVoucher) {
        String sql = SELECT_ALL + " WHERE MaVoucher = ? AND TrangThai = 1";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maVoucher);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<VoucherDTO> searchActive(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaVoucher LIKE ? OR TenVoucher LIKE ?)
                  AND TrangThai = 1
                ORDER BY TenVoucher
                """;
        List<VoucherDTO> list = new ArrayList<>();
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

    public List<VoucherDTO> search(String keyword) {
        String sql = SELECT_ALL + """
                WHERE (MaVoucher LIKE ? OR TenVoucher LIKE ?)
                ORDER BY TenVoucher
                """;
        List<VoucherDTO> list = new ArrayList<>();
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

    public boolean insert(VoucherDTO v) {
        String sql = """
                INSERT INTO VOUCHER
                (MaVoucher, TenVoucher, PhanTramGiam, SoTienGiam, GiamToiDa, DieuKienApDung,
                 NgayBatDau, NgayKetThuc, SoLuong, TrangThai)
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            fillInsertUpdate(ps, v);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(VoucherDTO v) {
        String sql = """
                UPDATE VOUCHER SET
                    TenVoucher=?, PhanTramGiam=?, SoTienGiam=?, GiamToiDa=?, DieuKienApDung=?,
                    NgayBatDau=?, NgayKetThuc=?, SoLuong=?, TrangThai=?
                WHERE MaVoucher=?
                """;

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getTenVoucher());
            ps.setInt(2, v.getPhanTramGiam());
            setBigDecimalOrNull(ps, 3, v.getSoTienGiam());
            setBigDecimalOrNull(ps, 4, v.getGiamToiDa());
            setBigDecimalOrNull(ps, 5, v.getDieuKienApDung());
            setLocalDateOrNull(ps, 6, v.getNgayBatDau());
            setLocalDateOrNull(ps, 7, v.getNgayKetThuc());
            ps.setInt(8, v.getSoLuong());
            ps.setInt(9, v.getTrangThai());
            ps.setString(10, v.getMaVoucher());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(String maVoucher) {
        String sql = "UPDATE VOUCHER SET TrangThai = 0 WHERE MaVoucher = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maVoucher);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restore(String maVoucher) {
        String sql = "UPDATE VOUCHER SET TrangThai = 1 WHERE MaVoucher = ?";

        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maVoucher);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fillInsertUpdate(PreparedStatement ps, VoucherDTO v) throws SQLException {
        ps.setString(1, v.getMaVoucher());
        ps.setString(2, v.getTenVoucher());
        ps.setInt(3, v.getPhanTramGiam());
        setBigDecimalOrNull(ps, 4, v.getSoTienGiam());
        setBigDecimalOrNull(ps, 5, v.getGiamToiDa());
        setBigDecimalOrNull(ps, 6, v.getDieuKienApDung());
        setLocalDateOrNull(ps, 7, v.getNgayBatDau());
        setLocalDateOrNull(ps, 8, v.getNgayKetThuc());
        ps.setInt(9, v.getSoLuong());
        ps.setInt(10, v.getTrangThai() == 0 ? 1 : v.getTrangThai());
    }

    public boolean decreaseQuantity(Connection c, String maVoucher, int soLuong) {
        String sql = """
                UPDATE VOUCHER
                SET SoLuong = SoLuong - ?
                WHERE MaVoucher = ? AND SoLuong >= ? AND TrangThai = 1
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            int sl = Math.max(0, soLuong);
            ps.setInt(1, sl);
            ps.setString(2, maVoucher);
            ps.setInt(3, sl);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public VoucherDTO findValidById(Connection c, String maVoucher, BigDecimal tongTien) {
        String sql = """
                SELECT MaVoucher, TenVoucher, PhanTramGiam, SoTienGiam, GiamToiDa, DieuKienApDung,
                       NgayBatDau, NgayKetThuc, SoLuong, TrangThai
                FROM VOUCHER
                WHERE MaVoucher = ?
                  AND TrangThai = 1
                  AND SoLuong > 0
                  AND (NgayBatDau IS NULL OR CAST(NgayBatDau AS DATE) <= CAST(GETDATE() AS DATE))
                  AND (NgayKetThuc IS NULL OR CAST(NgayKetThuc AS DATE) >= CAST(GETDATE() AS DATE))
                  AND (DieuKienApDung IS NULL OR ? >= DieuKienApDung)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maVoucher);
            ps.setBigDecimal(2, tongTien == null ? BigDecimal.ZERO : tongTien);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private VoucherDTO mapRow(ResultSet rs) throws SQLException {
        VoucherDTO v = new VoucherDTO();
        v.setMaVoucher(rs.getString("MaVoucher"));
        v.setTenVoucher(rs.getString("TenVoucher"));
        v.setPhanTramGiam(rs.getInt("PhanTramGiam"));
        v.setSoTienGiam(rs.getBigDecimal("SoTienGiam"));
        v.setGiamToiDa(rs.getBigDecimal("GiamToiDa"));
        v.setDieuKienApDung(rs.getBigDecimal("DieuKienApDung"));

        Date d1 = rs.getDate("NgayBatDau");
        v.setNgayBatDau(d1 == null ? null : d1.toLocalDate());

        Date d2 = rs.getDate("NgayKetThuc");
        v.setNgayKetThuc(d2 == null ? null : d2.toLocalDate());

        v.setSoLuong(rs.getInt("SoLuong"));
        v.setTrangThai(rs.getInt("TrangThai"));
        return v;
    }

    private void setLocalDateOrNull(PreparedStatement ps, int idx, LocalDate d) throws SQLException {
        if (d == null) ps.setNull(idx, Types.DATE);
        else ps.setDate(idx, Date.valueOf(d));
    }

    private void setBigDecimalOrNull(PreparedStatement ps, int idx, BigDecimal v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.DECIMAL);
        else ps.setBigDecimal(idx, v);
    }
}