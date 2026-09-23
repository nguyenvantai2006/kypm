package com.qlgiay.bus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.qlgiay.dao.ChiTietPhieuNhapDAO;
import com.qlgiay.dao.ChiTietTraNccDAO;
import com.qlgiay.dao.PhieuTraNccDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.dto.ChiTietTraNccDTO;
import com.qlgiay.dto.PhieuTraNccDTO;
import com.qlgiay.util.DBConnect;

public class TraHangNccBUS {
    private final PhieuTraNccDAO phieuTraDAO = new PhieuTraNccDAO();
    private final ChiTietTraNccDAO chiTietTraDAO = new ChiTietTraNccDAO();
    private final ChiTietPhieuNhapDAO chiTietNhapDAO = new ChiTietPhieuNhapDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private String lastError;

    public boolean createRequest(PhieuTraNccDTO phieu, ChiTietTraNccDTO chiTiet) {
        lastError = null;
        if (phieu == null || chiTiet == null || phieu.getMaPT() == null || phieu.getMaPN() == null
                || phieu.getMaNV() == null || phieu.getMaNCC() == null || phieu.getMaNCC().isBlank()
                || chiTiet.getMaSP() == null || chiTiet.getSoLuong() <= 0) {
            lastError = "Thiếu thông tin phiếu, nhân viên, nhà cung cấp hoặc sản phẩm.";
            return false;
        }
        Connection c = null;
        try {
            c = DBConnect.getConnection(); c.setAutoCommit(false);
            List<ChiTietPhieuNhapDTO> imported = chiTietNhapDAO.findByMaPN(phieu.getMaPN());
            ChiTietPhieuNhapDTO source = imported.stream().filter(x -> chiTiet.getMaSP().equals(x.getMaSP())).findFirst().orElse(null);
            if (source == null || chiTiet.getSoLuong() > source.getSoLuong()) {
                c.rollback();
                lastError = "Không tìm thấy sản phẩm trong phiếu nhập hoặc số lượng vượt quá số lượng đã nhập.";
                return false;
            }
            if (source.getGiaNhap() == null) {
                c.rollback();
                lastError = "Sản phẩm trong phiếu nhập chưa có giá nhập hợp lệ.";
                return false;
            }
            phieu.setNgayTao(LocalDate.now());
            phieu.setTongSoMatHang(chiTiet.getSoLuong());
            phieu.setTongTien(source.getGiaNhap().multiply(BigDecimal.valueOf(chiTiet.getSoLuong())));
            phieu.setTrangThai("Đang duyệt");
            chiTiet.setMaPT(phieu.getMaPT()); chiTiet.setGiaNhap(source.getGiaNhap());
            if (!phieuTraDAO.insert(c, phieu) || !chiTietTraDAO.insert(c, chiTiet)) {
                c.rollback();
                lastError = phieuTraDAO.getLastError();
                if (lastError == null || lastError.isBlank()) lastError = "Không thể ghi chi tiết hoàn trả.";
                return false;
            }
            c.commit(); return true;
        } catch (SQLException e) { rollback(c); lastError = e.getMessage(); return false; }
        finally { close(c); }
    }

    public List<PhieuTraNccDTO> getAllRequests() { return phieuTraDAO.findAll(); }
    public PhieuTraNccDTO findById(String maPT) { return phieuTraDAO.findById(maPT); }
    public List<ChiTietTraNccDTO> getDetails(String maPT) { return chiTietTraDAO.findByMaPT(maPT); }

    public String getLastError() { return lastError; }

    public boolean approve(String maPT, String nguoiXuLy) { return process(maPT, nguoiXuLy, "Đã duyệt", true); }
    public boolean reject(String maPT, String nguoiXuLy) { return process(maPT, nguoiXuLy, "Bị từ chối", false); }

    private boolean process(String maPT, String nguoiXuLy, String status, boolean deductStock) {
        Connection c = null;
        try {
            c = DBConnect.getConnection(); c.setAutoCommit(false);
            PhieuTraNccDTO p = phieuTraDAO.findById(maPT);
            if (p == null || !"Đang duyệt".equals(p.getTrangThai())) { c.rollback(); return false; }
            if (deductStock) {
                for (ChiTietTraNccDTO ct : chiTietTraDAO.findByMaPT(maPT)) {
                    if (!sanPhamDAO.decreaseStock(c, ct.getMaSP(), ct.getSoLuong())) { c.rollback(); return false; }
                }
            }
            if (!phieuTraDAO.updateStatus(c, maPT, status, nguoiXuLy)) { c.rollback(); return false; }
            c.commit(); return true;
        } catch (SQLException e) { rollback(c); lastError = e.getMessage(); return false; }
        finally { close(c); }
    }

    private void rollback(Connection c) { try { if (c != null) c.rollback(); } catch (SQLException ignored) {} }
    private void close(Connection c) { try { if (c != null) { c.setAutoCommit(true); c.close(); } } catch (SQLException ignored) {} }
}
