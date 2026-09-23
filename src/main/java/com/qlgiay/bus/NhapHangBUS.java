package com.qlgiay.bus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.qlgiay.dao.ChiTietPhieuNhapDAO;
import com.qlgiay.dao.PhieuNhapDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.dto.PhieuNhapDTO;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoTransactionData;

public class NhapHangBUS {
    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final ChiTietPhieuNhapDAO chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public boolean createImport(PhieuNhapDTO pn, List<ChiTietPhieuNhapDTO> items) {
        if (pn == null) return false;
        if (items == null || items.isEmpty()) return false;
        if (pn.getMaPN() == null || pn.getMaPN().trim().isEmpty()) return false;
        if (pn.getMaNV() == null || pn.getMaNV().trim().isEmpty()) return false;
        if (pn.getMaNCC() == null || pn.getMaNCC().trim().isEmpty()) return false;

        if (DBConnect.isDemoMode()) {
            BigDecimal total = BigDecimal.ZERO;
            int quantity = 0;
            for (ChiTietPhieuNhapDTO item : items) {
                if (item == null || item.getSoLuong() <= 0 || item.getGiaNhap() == null || item.getGiaNhap().signum() < 0) return false;
                item.setMaPN(pn.getMaPN());
                quantity += item.getSoLuong();
                total = total.add(item.getGiaNhap().multiply(BigDecimal.valueOf(item.getSoLuong())));
            }
            pn.setTongSoMatHang(quantity);
            pn.setTongTien(total.setScale(0, RoundingMode.HALF_UP));
            return DemoTransactionData.addImport(pn, items);
        }

        Connection c = null;
        try {
            c = DBConnect.getConnection();
            c.setAutoCommit(false);

            int tongSoMatHang = 0;
            BigDecimal tongTien = BigDecimal.ZERO;

            for (ChiTietPhieuNhapDTO ct : items) {
                if (ct == null) {
                    c.rollback();
                    return false;
                }
                if (ct.getMaSP() == null || ct.getMaSP().trim().isEmpty()) {
                    c.rollback();
                    return false;
                }
                if (ct.getSoLuong() <= 0) {
                    c.rollback();
                    return false;
                }
                if (ct.getGiaNhap() == null || ct.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
                    c.rollback();
                    return false;
                }

                tongSoMatHang += ct.getSoLuong();
                tongTien = tongTien.add(ct.getGiaNhap().multiply(new BigDecimal(ct.getSoLuong())));
            }

            tongTien = tongTien.setScale(0, RoundingMode.HALF_UP);

            pn.setTongSoMatHang(tongSoMatHang);
            pn.setTongTien(tongTien);

            boolean ok = phieuNhapDAO.insert(c, pn);
            if (!ok) {
                c.rollback();
                return false;
            }

            for (ChiTietPhieuNhapDTO ct : items) {
                if (ct.getMaPN() == null || ct.getMaPN().trim().isEmpty()) {
                    ct.setMaPN(pn.getMaPN());
                }

                ok = chiTietPhieuNhapDAO.insert(c, ct);
                if (!ok) {
                    c.rollback();
                    return false;
                }

                ok = sanPhamDAO.increaseStock(c, ct.getMaSP().trim(), ct.getSoLuong());
                if (!ok) {
                    c.rollback();
                    return false;
                }

                ok = sanPhamDAO.updateDerivedSalePrice(c, ct.getMaSP().trim(), ct.getGiaNhap());
                if (!ok) {
                    c.rollback();
                    return false;
                }
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (c != null) c.rollback();
            } catch (SQLException ignored) {
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public List<PhieuNhapDTO> getAllImports() {
        if (DBConnect.isDemoMode()) return DemoTransactionData.imports();
        return phieuNhapDAO.findAll();
    }

    public List<PhieuNhapDTO> searchImports(String keyword) {
        if (DBConnect.isDemoMode()) return DemoTransactionData.imports().stream().filter(p -> keyword == null || keyword.isBlank() || p.getMaPN().contains(keyword.trim()) || p.getMaNCC().contains(keyword.trim())).toList();
        return phieuNhapDAO.search(keyword);
    }

    public PhieuNhapDTO findImportById(String maPN) {
        if (DBConnect.isDemoMode()) return DemoTransactionData.imports().stream().filter(p -> p.getMaPN().equalsIgnoreCase(maPN)).findFirst().orElse(null);
        return phieuNhapDAO.findById(maPN);
    }

    public List<ChiTietPhieuNhapDTO> getImportDetails(String maPN) {
        if (DBConnect.isDemoMode()) return DemoTransactionData.importDetails(maPN);
        return chiTietPhieuNhapDAO.findByMaPN(maPN);
    }
}