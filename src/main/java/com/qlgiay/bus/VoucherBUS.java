package com.qlgiay.bus;

import com.qlgiay.dao.VoucherDAO;
import com.qlgiay.dto.VoucherDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VoucherBUS {
    private final VoucherDAO voucherDAO = new VoucherDAO();

    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isValid(VoucherDTO v) {
        if (v == null)
            return false;

        String ma = trimOrEmpty(v.getMaVoucher());
        String ten = trimOrEmpty(v.getTenVoucher());

        if (ma.isEmpty())
            return false;
        if (ten.isEmpty())
            return false;

        v.setMaVoucher(ma);
        v.setTenVoucher(ten);

        if (v.getSoLuong() < 0)
            return false;

        if (v.getTrangThai() < 0 || v.getTrangThai() > 3)
            v.setTrangThai(1);

        int pt = v.getPhanTramGiam();
        if (pt < 0 || pt > 100)
            return false;

        BigDecimal soTienGiam = v.getSoTienGiam();
        if (soTienGiam != null && soTienGiam.compareTo(BigDecimal.ZERO) < 0)
            return false;

        BigDecimal giamToiDa = v.getGiamToiDa();
        if (giamToiDa != null && giamToiDa.compareTo(BigDecimal.ZERO) < 0)
            return false;

        BigDecimal dieuKien = v.getDieuKienApDung();
        if (dieuKien != null && dieuKien.compareTo(BigDecimal.ZERO) < 0)
            return false;

        boolean hasPercent = pt > 0;
        boolean hasMoney = soTienGiam != null && soTienGiam.compareTo(BigDecimal.ZERO) > 0;

        if (!hasPercent && !hasMoney)
            return false;

        LocalDate start = v.getNgayBatDau();
        LocalDate end = v.getNgayKetThuc();
        if (start != null && end != null && end.isBefore(start))
            return false;

        if (hasPercent) {
            if (giamToiDa != null && giamToiDa.compareTo(BigDecimal.ZERO) <= 0)
                return false;
        }

        return true;
    }

    public List<VoucherDTO> getAll() {
        return voucherDAO.findAll();
    }

    public List<VoucherDTO> getAllActive() {
        return voucherDAO.findAllActive();
    }

    public VoucherDTO findById(String maVoucher) {
        String id = trimOrEmpty(maVoucher);
        if (id.isEmpty())
            return null;
        return voucherDAO.findById(id);
    }

    public boolean addVoucher(VoucherDTO v) {
        if (!isValid(v))
            return false;

        if (voucherDAO.findById(v.getMaVoucher()) != null)
            return false;

        return voucherDAO.insert(v);
    }

    public boolean updateVoucher(VoucherDTO v) {
        if (!isValid(v))
            return false;

        if (voucherDAO.findById(v.getMaVoucher()) == null)
            return false;

        return voucherDAO.update(v);
    }

    public boolean deleteVoucher(String maVoucher) {
        String id = trimOrEmpty(maVoucher);
        if (id.isEmpty())
            return false;

        return voucherDAO.softDelete(id);
    }

    public boolean restoreVoucher(String maVoucher) {
        String id = trimOrEmpty(maVoucher);
        if (id.isEmpty())
            return false;

        return voucherDAO.restore(id);
    }

    public List<VoucherDTO> search(String keyword) {
        return voucherDAO.search(keyword);
    }

    public List<VoucherDTO> searchActive(String keyword) {
        return voucherDAO.searchActive(keyword);
    }

    public void autoUpdateVoucherStatuses() {
        List<VoucherDTO> list = getAll();
        if (list == null)
            return;

        LocalDate now = LocalDate.now();
        for (VoucherDTO v : list) {
            int currentState = v.getTrangThai();
            int correctState = currentState;

            if (v.getTrangThai() != 3) {
                if (v.getNgayBatDau() != null && v.getNgayBatDau().isAfter(now)) {
                    correctState = 2;
                } else if (v.getNgayKetThuc() != null && v.getNgayKetThuc().isBefore(now)) {
                    correctState = 0;
                } else if (currentState == 2
                        && v.getNgayBatDau() != null
                        && !v.getNgayBatDau().isAfter(now)) {
                    correctState = 1;
                }
            } else if (v.getNgayKetThuc() != null && v.getNgayKetThuc().isBefore(now)) {
                correctState = 0;
            }

            if (currentState != correctState) {
                v.setTrangThai(correctState);
                updateVoucher(v);
            }
        }
    }
}