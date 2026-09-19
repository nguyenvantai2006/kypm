package com.qlgiay.bus;

import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.SanPhamDTO;

import java.math.BigDecimal;
import java.util.List;

public class SanPhamBUS {
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private boolean isValid(SanPhamDTO sp) {
        if (sp == null) return false;

        if (sp.getMaSP() == null || sp.getMaSP().trim().isEmpty())
            return false;

        if (sp.getTenSP() == null || sp.getTenSP().trim().isEmpty())
            return false;

        if (sp.getSoLuong() < 0)
            return false;

        if (sp.getDonGia() == null || sp.getDonGia().compareTo(BigDecimal.ZERO) < 0)
            return false;

        return true;
    }

    public List<SanPhamDTO> getAllActive() {
        return sanPhamDAO.findAllActive();
    }

    public List<SanPhamDTO> getBySupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) return List.of();
        return sanPhamDAO.findBySupplier(maNCC.trim());
    }

    public List<SanPhamDTO> getAll() {
        return sanPhamDAO.findAll();
    }

    public SanPhamDTO findById(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            return null;

        return sanPhamDAO.findById(maSP.trim());
    }

    public boolean addProduct(SanPhamDTO sp) {
        if (!isValid(sp))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        if (sanPhamDAO.findById(sp.getMaSP()) != null)
            return false;

        return sanPhamDAO.insert(sp);
    }

    public boolean updateProduct(SanPhamDTO sp) {
        if (!isValid(sp))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        return sanPhamDAO.update(sp);
    }

    public boolean deleteProduct(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            return false;

        return sanPhamDAO.softDelete(maSP.trim());
    }

    public boolean restoreProduct(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            return false;

        return sanPhamDAO.restore(maSP.trim());
    }

    public List<SanPhamDTO> search(String keyword) {
        return sanPhamDAO.search(keyword);
    }

    public List<SanPhamDTO> searchActive(String keyword) {
        return sanPhamDAO.searchActive(keyword);
    }
}