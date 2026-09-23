package com.qlgiay.bus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.HoaDonDAO;
import com.qlgiay.dao.KhachHangDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dao.VoucherDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.VoucherDTO;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoTransactionData;

public class BanHangBUS {
    private static final BigDecimal DIEM_TO_VND = new BigDecimal("1000");
    private static final BigDecimal VND_PER_POINT = new BigDecimal("10000");
    private static final BigDecimal MAX_POINT_DISCOUNT_RATE = new BigDecimal("0.30");

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public boolean createInvoice(HoaDonDTO hd, List<ChiTietHoaDonDTO> items, int diemMuonDung) {
        if (hd == null || items == null || items.isEmpty()) return false;
        if (hd.getMaNV() == null || hd.getMaNV().trim().isEmpty()) return false;
        if (hd.getMaHD() == null || hd.getMaHD().trim().isEmpty()) return false;

        if (DBConnect.isDemoMode()) {
            BigDecimal subtotal = calcSubTotal(items);
            if (subtotal.signum() <= 0) return false;
            for (ChiTietHoaDonDTO item : items) {
                if (item == null || item.getMaSP() == null || item.getDonGia() == null || item.getSoLuong() <= 0) return false;
                item.setMaHD(hd.getMaHD());
            }
            hd.setTongTien(subtotal);
            return DemoTransactionData.addInvoice(hd, items);
        }

        Connection c = null;
        try {
            c = DBConnect.getConnection();
            c.setAutoCommit(false);

            BigDecimal tamTinh = calcSubTotal(items);
            if (tamTinh.compareTo(BigDecimal.ZERO) <= 0) {
                c.rollback();
                return false;
            }

            VoucherDTO voucher = null;
            BigDecimal giamVoucher = BigDecimal.ZERO;
            if (hd.getMaVoucher() != null && !hd.getMaVoucher().trim().isEmpty()) {
                voucher = voucherDAO.findValidById(c, hd.getMaVoucher().trim(), tamTinh);
                if (voucher == null) {
                    c.rollback();
                    return false;
                }
                giamVoucher = calcVoucherDiscount(voucher, tamTinh);
            }

            BigDecimal tongSauVoucher = tamTinh.subtract(giamVoucher);
            if (tongSauVoucher.compareTo(BigDecimal.ZERO) < 0) tongSauVoucher = BigDecimal.ZERO;

            int diemThucTeDung = 0;
            BigDecimal giamDiem = BigDecimal.ZERO;

            String maKH = (hd.getMaKH() == null || hd.getMaKH().trim().isEmpty()) ? null : hd.getMaKH().trim();
            if (maKH != null && diemMuonDung > 0) {
                int diemHienCo = khachHangDAO.getPoints(c, maKH);
                int diemYeuCau = Math.max(0, diemMuonDung);

                BigDecimal maxGiamTheoDiem = tongSauVoucher.multiply(MAX_POINT_DISCOUNT_RATE)
                        .setScale(0, RoundingMode.FLOOR);

                int diemToiDaTheo30 = maxGiamTheoDiem
                        .divide(DIEM_TO_VND, 0, RoundingMode.FLOOR)
                        .intValue();

                diemThucTeDung = Math.min(diemYeuCau, Math.min(diemHienCo, diemToiDaTheo30));
                if (diemThucTeDung > 0) {
                    giamDiem = new BigDecimal(diemThucTeDung).multiply(DIEM_TO_VND);
                }
            }

            BigDecimal tongCuoi = tongSauVoucher.subtract(giamDiem);
            if (tongCuoi.compareTo(BigDecimal.ZERO) < 0) tongCuoi = BigDecimal.ZERO;

            hd.setTongTien(tongCuoi);

            boolean ok = hoaDonDAO.insert(c, hd);
            if (!ok) {
                c.rollback();
                return false;
            }

            for (ChiTietHoaDonDTO ct : items) {
                if (ct.getMaHD() == null || ct.getMaHD().trim().isEmpty()) ct.setMaHD(hd.getMaHD());
                if (ct.getMaSP() == null || ct.getMaSP().trim().isEmpty() || ct.getSoLuong() <= 0 || ct.getDonGia() == null || ct.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                    c.rollback();
                    return false;
                }

                ok = chiTietHoaDonDAO.insert(c, ct);
                if (!ok) {
                    c.rollback();
                    return false;
                }

                ok = sanPhamDAO.decreaseStock(c, ct.getMaSP().trim(), ct.getSoLuong());
                if (!ok) {
                    c.rollback();
                    return false;
                }
            }

            if (voucher != null) {
                ok = voucherDAO.decreaseQuantity(c, voucher.getMaVoucher(), 1);
                if (!ok) {
                    c.rollback();
                    return false;
                }
            }

            if (maKH != null) {
                int diemCong = calcEarnedPoints(tongCuoi);
                if (diemCong > 0) {
                    ok = khachHangDAO.addPoints(c, maKH, diemCong);
                    if (!ok) {
                        c.rollback();
                        return false;
                    }
                }

                if (diemThucTeDung > 0) {
                    ok = khachHangDAO.usePoints(c, maKH, diemThucTeDung);
                    if (!ok) {
                        c.rollback();
                        return false;
                    }
                }
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (c != null) c.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private BigDecimal calcSubTotal(List<ChiTietHoaDonDTO> items) {
        BigDecimal sum = BigDecimal.ZERO;
        for (ChiTietHoaDonDTO ct : items) {
            if (ct == null) continue;
            if (ct.getDonGia() == null) continue;
            if (ct.getSoLuong() <= 0) continue;
            sum = sum.add(ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong())));
        }
        return sum.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal calcVoucherDiscount(VoucherDTO v, BigDecimal tamTinh) {
        if (v == null) return BigDecimal.ZERO;
        if (tamTinh == null || tamTinh.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        BigDecimal discount = BigDecimal.ZERO;

        if (v.getPhanTramGiam() > 0) {
            BigDecimal rate = new BigDecimal(v.getPhanTramGiam()).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            discount = tamTinh.multiply(rate);

            if (v.getGiamToiDa() != null && v.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                discount = discount.min(v.getGiamToiDa());
            }
        } else if (v.getSoTienGiam() != null && v.getSoTienGiam().compareTo(BigDecimal.ZERO) > 0) {
            discount = v.getSoTienGiam();
        }

        if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;
        if (discount.compareTo(tamTinh) > 0) discount = tamTinh;

        return discount.setScale(0, RoundingMode.FLOOR);
    }

    private int calcEarnedPoints(BigDecimal tongTienSauGiam) {
        if (tongTienSauGiam == null) return 0;
        if (tongTienSauGiam.compareTo(BigDecimal.ZERO) <= 0) return 0;

        return tongTienSauGiam.divide(VND_PER_POINT, 0, RoundingMode.FLOOR).intValue();
    }
}