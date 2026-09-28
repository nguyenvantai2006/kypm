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
                // TÍNH BÌNH QUÂN GIA QUYỀN & LÀM TRÒN GIÁ BÁN
                com.qlgiay.dto.SanPhamDTO spCu = sanPhamDAO.findById(c, ct.getMaSP().trim());
                if (spCu != null) {
                    int tonKhoCu = spCu.getSoLuong();
                    BigDecimal phanTramLoiNhuan = spCu.getPhanTramLoiNhuan() != null
                            ? spCu.getPhanTramLoiNhuan()
                            : new BigDecimal("20");
                    BigDecimal tyLeLoiNhuan = BigDecimal.ONE.add(
                            phanTramLoiNhuan.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

                    // Tính giá vốn cũ
                    BigDecimal giaVonCu = BigDecimal.ZERO;
                    if (tonKhoCu > 0 && spCu.getDonGia() != null
                            && spCu.getDonGia().compareTo(BigDecimal.ZERO) > 0) {
                        giaVonCu = spCu.getDonGia().divide(tyLeLoiNhuan, 2, RoundingMode.HALF_UP);
                    }

                    // Tính trung bình gia quyền
                    int tongTonKhoMoi = tonKhoCu + ct.getSoLuong();
                    BigDecimal tongGiaTriCu = giaVonCu.multiply(new BigDecimal(tonKhoCu));
                    BigDecimal tongGiaTriMoiNhap = ct.getGiaNhap().multiply(new BigDecimal(ct.getSoLuong()));

                    BigDecimal giaVonTrungBinh = tongGiaTriCu.add(tongGiaTriMoiNhap)
                            .divide(new BigDecimal(tongTonKhoMoi), 2, RoundingMode.HALF_UP);

                    // Tính giá bán lý thuyết
                    BigDecimal giaBanLyThuyet = giaVonTrungBinh.multiply(tyLeLoiNhuan);

                    // THUẬT TOÁN LÀM TRÒN ĐẾN 50.000đ GẦN NHẤT
                    long rounded = Math.round(giaBanLyThuyet.doubleValue() / 50000.0) * 50000L;
                    BigDecimal giaBanMoi = new BigDecimal(rounded);

                    // Lưu giá bán đã làm tròn đẹp mắt vào Database
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