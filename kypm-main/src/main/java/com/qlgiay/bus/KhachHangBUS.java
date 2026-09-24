package com.qlgiay.bus;

import com.qlgiay.dao.KhachHangDAO;
import com.qlgiay.dto.KhachHangDTO;

import java.util.List;

public class KhachHangBUS {
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    private boolean isValid(KhachHangDTO kh) {
        if (kh == null)
            return false;

        if (kh.getMaKH() == null || kh.getMaKH().trim().isEmpty())
            return false;

        if (kh.getTenKH() == null || kh.getTenKH().trim().isEmpty())
            return false;

        String sdt = kh.getSdt() != null ? kh.getSdt().trim() : "";

        if (sdt.isEmpty())
            return false;

        if (!sdt.matches("^0\\d{9}$"))
            return false;

        kh.setSdt(sdt);

        return true;
    }

    public List<KhachHangDTO> getAll() {
        return khachHangDAO.findAll();
    }

    public KhachHangDTO findById(String maKH) {
        if (maKH == null || maKH.trim().isEmpty())
            return null;

        return khachHangDAO.findById(maKH.trim());
    }

    public KhachHangDTO findByPhone(String sdt) {
        if (sdt == null || sdt.trim().isEmpty())
            return null;

        return khachHangDAO.findByPhone(sdt.trim());
    }

    public boolean addCustomer(KhachHangDTO kh) {
        if (!isValid(kh))
            return false;

        kh.setMaKH(kh.getMaKH().trim());

        if (khachHangDAO.findById(kh.getMaKH()) != null)
            return false;

        if (khachHangDAO.findByPhone(kh.getSdt()) != null)
            return false;

        return khachHangDAO.insert(kh);
    }

    public boolean updateCustomer(KhachHangDTO kh) {
        if (!isValid(kh))
            return false;

        kh.setMaKH(kh.getMaKH().trim());

        return khachHangDAO.update(kh);
    }

    public boolean deleteCustomer(String maKH) {
        if (maKH == null || maKH.trim().isEmpty())
            return false;

        return khachHangDAO.softDelete(maKH.trim());
    }

    public List<KhachHangDTO> search(String keyword) {
        return khachHangDAO.search(keyword);
    }
}