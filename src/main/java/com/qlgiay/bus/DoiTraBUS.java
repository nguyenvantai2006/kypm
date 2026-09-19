package com.qlgiay.bus;

import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.DoiTraDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.DoiTraDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class DoiTraBUS {
    private final DoiTraDAO doiTraDAO = new DoiTraDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public boolean createReturn(DoiTraDTO dt) {
        if (dt == null) return false;
        if (dt.getMaDT() == null || dt.getMaDT().trim().isEmpty()) return false;
        if (dt.getMaHD() == null || dt.getMaHD().trim().isEmpty()) return false;
        if (dt.getMaNV() == null || dt.getMaNV().trim().isEmpty()) return false;
        if (dt.getMaSP() == null || dt.getMaSP().trim().isEmpty()) return false;
        if (dt.getSoLuong() <= 0) return false;
        if (dt.getLyDo() == null || dt.getLyDo().trim().isEmpty()) return false;
        if (dt.getTinhTrang() == null || dt.getTinhTrang().trim().isEmpty()) return false;

        Connection c = null;
        try {
            c = DBConnect.getConnection();
            c.setAutoCommit(false);

            ChiTietHoaDonDTO ct = chiTietHoaDonDAO.findById(c, dt.getMaHD().trim(), dt.getMaSP().trim());
            if (ct == null) {
                c.rollback();
                return false;
            }

            int soLuongDaMua = ct.getSoLuong();
            if (dt.getSoLuong() > soLuongDaMua) {
                c.rollback();
                return false;
            }

            BigDecimal donGiaLucBan = ct.getDonGia();
            if (donGiaLucBan == null || donGiaLucBan.compareTo(BigDecimal.ZERO) < 0) {
                c.rollback();
                return false;
            }

            BigDecimal tongTienHoan = donGiaLucBan.multiply(new BigDecimal(dt.getSoLuong()));
            dt.setTongTienHoan(tongTienHoan);

            if (dt.getNgayDoiTra() == null) dt.setNgayDoiTra(LocalDate.now());

            boolean ok = doiTraDAO.insert(c, dt);
            if (!ok) {
                c.rollback();
                return false;
            }

            ok = sanPhamDAO.increaseStock(c, dt.getMaSP().trim(), dt.getSoLuong());
            if (!ok) {
                c.rollback();
                return false;
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (c != null) c.rollback();
            } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException ignored) {}
        }
    }
}