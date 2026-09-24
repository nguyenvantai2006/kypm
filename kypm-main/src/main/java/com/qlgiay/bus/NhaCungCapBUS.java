package com.qlgiay.bus;

import java.util.List;

import com.qlgiay.dao.NhaCungCapDAO;
import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoTransactionData;

public class NhaCungCapBUS {
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    private boolean isValid(NhaCungCapDTO ncc) {
        if (ncc == null) return false;

        if (ncc.getMaNCC() == null || ncc.getMaNCC().trim().isEmpty())
            return false;

        if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty())
            return false;

        String sdt = ncc.getSdt() != null ? ncc.getSdt().trim() : "";
        if (sdt.isEmpty())
            return false;

        if (!sdt.matches("^0\\d{9}$"))
            return false;

        ncc.setSdt(sdt);

        return true;
    }

    public List<NhaCungCapDTO> getAll() {
        if (DBConnect.isDemoMode()) return DemoTransactionData.suppliers(false);
        return nhaCungCapDAO.findAll();
    }

    public List<NhaCungCapDTO> getAllActive() {
        if (DBConnect.isDemoMode()) return DemoTransactionData.suppliers(true);
        return nhaCungCapDAO.findAllActive();
    }

    public NhaCungCapDTO findById(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return null;

        if (DBConnect.isDemoMode()) return DemoTransactionData.findSupplier(maNCC.trim());
        return nhaCungCapDAO.findById(maNCC.trim());
    }

    public boolean addSupplier(NhaCungCapDTO ncc) {
        if (!isValid(ncc))
            return false;

        ncc.setMaNCC(ncc.getMaNCC().trim());

        if (findById(ncc.getMaNCC()) != null)
            return false;

        if (DBConnect.isDemoMode()) return DemoTransactionData.addSupplier(ncc);
        return nhaCungCapDAO.insert(ncc);
    }

    public boolean updateSupplier(NhaCungCapDTO ncc) {
        if (!isValid(ncc))
            return false;

        ncc.setMaNCC(ncc.getMaNCC().trim());

        if (DBConnect.isDemoMode()) return DemoTransactionData.updateSupplier(ncc);
        return nhaCungCapDAO.update(ncc);
    }

    public boolean deleteSupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return false;

        if (DBConnect.isDemoMode()) return DemoTransactionData.setSupplierStatus(maNCC.trim(), 0);
        return nhaCungCapDAO.softDelete(maNCC.trim());
    }

    public boolean restoreSupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return false;

        if (DBConnect.isDemoMode()) return DemoTransactionData.setSupplierStatus(maNCC.trim(), 1);
        return nhaCungCapDAO.restore(maNCC.trim());
    }

    public List<NhaCungCapDTO> search(String keyword) {
        if (DBConnect.isDemoMode()) return DemoTransactionData.searchSuppliers(keyword, false);
        return nhaCungCapDAO.search(keyword);
    }

    public List<NhaCungCapDTO> searchActive(String keyword) {
        if (DBConnect.isDemoMode()) return DemoTransactionData.searchSuppliers(keyword, true);
        return nhaCungCapDAO.searchActive(keyword);
    }
}