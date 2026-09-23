package com.qlgiay.bus;

import java.util.List;

import com.qlgiay.dao.NhaCungCapDAO;
import com.qlgiay.dto.NhaCungCapDTO;

public class NhaCungCapBUS {
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    private boolean isValid(NhaCungCapDTO ncc) {
        if (ncc == null)
            return false;

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
        return nhaCungCapDAO.findAll();
    }

    public List<NhaCungCapDTO> getAllActive() {
        return nhaCungCapDAO.findAllActive();
    }

    public NhaCungCapDTO findById(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return null;

        return nhaCungCapDAO.findById(maNCC.trim());
    }

    public boolean addSupplier(NhaCungCapDTO ncc) {
        if (!isValid(ncc))
            return false;

        ncc.setMaNCC(ncc.getMaNCC().trim());

        if (findById(ncc.getMaNCC()) != null)
            return false;

        return nhaCungCapDAO.insert(ncc);
    }

    public boolean updateSupplier(NhaCungCapDTO ncc) {
        if (!isValid(ncc))
            return false;

        ncc.setMaNCC(ncc.getMaNCC().trim());

        return nhaCungCapDAO.update(ncc);
    }

    public boolean deleteSupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return false;

        return nhaCungCapDAO.softDelete(maNCC.trim());
    }

    public boolean restoreSupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return false;

        return nhaCungCapDAO.restore(maNCC.trim());
    }

    public List<NhaCungCapDTO> search(String keyword) {
        return nhaCungCapDAO.search(keyword);
    }

    public List<NhaCungCapDTO> searchActive(String keyword) {
        return nhaCungCapDAO.searchActive(keyword);
    }
}