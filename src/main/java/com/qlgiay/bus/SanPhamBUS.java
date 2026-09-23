package com.qlgiay.bus;

import java.math.BigDecimal;
import java.util.List;

import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoProductData;

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
        if (DBConnect.isDemoMode()) return DemoProductData.search("", true);
        return sanPhamDAO.findAllActive();
    }

    public List<SanPhamDTO> getBySupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) return List.of();
        if (DBConnect.isDemoMode()) return DemoProductData.search("", true);
        return sanPhamDAO.findBySupplier(maNCC.trim());
    }

    public List<SanPhamDTO> getAll() {
        if (DBConnect.isDemoMode()) return DemoProductData.all();
        return sanPhamDAO.findAll();
    }

    public SanPhamDTO findById(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            return null;

        if (DBConnect.isDemoMode()) return DemoProductData.findById(maSP.trim());
        return sanPhamDAO.findById(maSP.trim());
    }

    public boolean addProduct(SanPhamDTO sp) {
        if (!isValid(sp))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        if (findById(sp.getMaSP()) != null)
            return false;

        if (DBConnect.isDemoMode()) return DemoProductData.insert(sp);
        return sanPhamDAO.insert(sp);
    }

    public boolean updateProduct(SanPhamDTO sp) {
        if (!isValid(sp))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        if (DBConnect.isDemoMode()) return DemoProductData.update(sp);
        return sanPhamDAO.update(sp);
    }

    public boolean deleteProduct(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            return false;

        if (DBConnect.isDemoMode()) return DemoProductData.setStatus(maSP.trim(), 0);
        return sanPhamDAO.softDelete(maSP.trim());
    }

    public boolean restoreProduct(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            return false;

        if (DBConnect.isDemoMode()) return DemoProductData.setStatus(maSP.trim(), 1);
        return sanPhamDAO.restore(maSP.trim());
    }

    public List<SanPhamDTO> search(String keyword) {
        if (DBConnect.isDemoMode()) return DemoProductData.search(keyword, false);
        return sanPhamDAO.search(keyword);
    }

    public List<SanPhamDTO> searchActive(String keyword) {
        if (DBConnect.isDemoMode()) return DemoProductData.search(keyword, true);
        return sanPhamDAO.searchActive(keyword);
    }
}