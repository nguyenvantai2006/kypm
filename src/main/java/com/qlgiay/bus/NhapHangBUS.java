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

public class NhapHangBUS {
    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final ChiTietPhieuNhapDAO chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public boolean createImport(PhieuNhapDTO pn, List<ChiTietPhieuNhapDTO> items) {
        if (pn == null)
            return false;
        if (items == null || items.isEmpty())
            return false;
        if (pn.getMaPN() == null || pn.getMaPN().trim().isEmpty())
            return false;
        if (pn.getMaNV() == null || pn.getMaNV().trim().isEmpty())
            return false;
        if (pn.getMaNCC() == null || pn.getMaNCC().trim().isEmpty())
            return false;

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
                // Cập nhật giá bán chính thức theo thay đổi giá nhập.
                com.qlgiay.dto.SanPhamDTO spCu = sanPhamDAO.findById(c, ct.getMaSP().trim());
                if (spCu != null) {
                    BigDecimal phanTramLoiNhuan = spCu.getPhanTramLoiNhuan() != null
                            ? spCu.getPhanTramLoiNhuan()
                            : new BigDecimal("20");
                    BigDecimal giaBanMoi;
                    if (spCu.getGiaNhap() == null || spCu.getDonGia() == null) {
                        giaBanMoi = com.qlgiay.dto.SanPhamDTO.tinhGiaBan(ct.getGiaNhap(), phanTramLoiNhuan);
                    } else {
                        giaBanMoi = com.qlgiay.dto.SanPhamDTO.tinhGiaBanTheoChinhSachGiaNhap(
                                spCu.getGiaNhap(), ct.getGiaNhap(), spCu.getDonGia(), phanTramLoiNhuan);
                    }
                    if (giaBanMoi == null) {
                        c.rollback();
                        return false;
                    }

                    // Lưu giá bán chính thức theo chính sách giá nhập.
                    String sqlUpdatePrice = "UPDATE SAN_PHAM SET DonGia = ? WHERE MaSP = ?";
                    try (java.sql.PreparedStatement psPrice = c.prepareStatement(sqlUpdatePrice)) {
                        psPrice.setBigDecimal(1, giaBanMoi);
                        psPrice.setString(2, ct.getMaSP().trim());
                        if (psPrice.executeUpdate() <= 0) {
                            c.rollback();
                            return false;
                        }
                    }
                }

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
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (c != null)
                    c.rollback();
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
        return phieuNhapDAO.findAll();
    }

    public List<PhieuNhapDTO> searchImports(String keyword) {
        return phieuNhapDAO.search(keyword);
    }

    public PhieuNhapDTO findImportById(String maPN) {
        return phieuNhapDAO.findById(maPN);
    }

    public List<ChiTietPhieuNhapDTO> getImportDetails(String maPN) {
        return chiTietPhieuNhapDAO.findByMaPN(maPN);
    }
}