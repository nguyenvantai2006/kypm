package com.qlgiay.bus;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.SanPhamDTO;

public class SanPhamBUS {
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private boolean isValid(SanPhamDTO sp) {
        return isValid(sp, false);
    }

    private boolean isValid(SanPhamDTO sp, boolean allowMissingSellingPrice) {
        if (sp == null)
            return false;

        if (sp.getPhanTramLoiNhuan() == null
                || sp.getPhanTramLoiNhuan().compareTo(BigDecimal.ZERO) <= 0
                || sp.getPhanTramLoiNhuan().compareTo(BigDecimal.valueOf(100)) > 0) {
            return false;
        }
        if (sp.getGiaNhap() != null && sp.getGiaNhap().compareTo(BigDecimal.ZERO) <= 0)
            return false;
        if (sp.getDonGia() == null) {
            if (!allowMissingSellingPrice || sp.getGiaNhap() != null)
                return false;
        } else if (sp.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (sp.getMaSP() == null || sp.getMaSP().trim().isEmpty())
            return false;

        if (sp.getTenSP() == null || sp.getTenSP().trim().isEmpty())
            return false;

        if (sp.getNgaySanXuat() == null || !sp.getNgaySanXuat().isBefore(LocalDate.now()))
            return false;

        if (sp.getSoLuong() < 0)
            return false;

        return true;
    }

    public List<SanPhamDTO> getAllActive() {
        return sanPhamDAO.findAllActive();
    }

    public List<SanPhamDTO> getBySupplier(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            return List.of();
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
        if (!prepareNewSku(sp))
            return false;

        syncImportPrice(sp);
        sp.setDonGia(SanPhamDTO.tinhGiaBan(sp.getGiaNhap(), sp.getPhanTramLoiNhuan()));
        if (!isValid(sp))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        if (findById(sp.getMaSP()) != null)
            return false;

        return sanPhamDAO.insert(sp);
    }

    public boolean addProductWithoutPrice(SanPhamDTO sp) {
        if (!prepareNewSku(sp) || !isValid(sp, true))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        if (findById(sp.getMaSP()) != null)
            return false;

        return sanPhamDAO.insert(sp);
    }

    public boolean updateProduct(SanPhamDTO sp) {
        if (sp == null || sp.getMaSP() == null || sp.getMaSP().trim().isEmpty())
            return false;

        SanPhamDTO existing = findById(sp.getMaSP().trim());
        if (existing == null)
            return false;

        syncImportPrice(sp);
        boolean giaNhapGiam = existing.getGiaNhap() != null && sp.getGiaNhap() != null
                && sp.getGiaNhap().compareTo(existing.getGiaNhap()) < 0;
        boolean loiNhuanThayDoi = existing.getPhanTramLoiNhuan() == null
                || existing.getPhanTramLoiNhuan().compareTo(sp.getPhanTramLoiNhuan()) != 0;
        if (giaNhapGiam) {
            sp.setDonGia(existing.getDonGia());
        } else if (loiNhuanThayDoi) {
            sp.setDonGia(SanPhamDTO.tinhGiaBan(sp.getGiaNhap(), sp.getPhanTramLoiNhuan()));
        } else {
            sp.setDonGia(SanPhamDTO.tinhGiaBanTheoChinhSachGiaNhap(
                    existing.getGiaNhap(), sp.getGiaNhap(), existing.getDonGia(), sp.getPhanTramLoiNhuan()));
        }
        if (!isValid(sp))
            return false;

        sp.setMaSP(sp.getMaSP().trim());

        return sanPhamDAO.update(sp);
    }

    private boolean prepareNewSku(SanPhamDTO sp) {
        if (sp == null || isBlank(sp.getMaSP()) || isBlank(sp.getSize()) || isBlank(sp.getMauSac()))
            return false;

        String sku = buildSku(sp.getMaSP(), sp.getSize(), sp.getMauSac());
        if (sku == null)
            return false;

        sp.setMaSP(sku);
        return true;
    }

    public String buildSku(String maSP, String size, String mauSac) {
        if (isBlank(maSP) || isBlank(size) || isBlank(mauSac))
            return null;

        String base = skuToken(maSP.split("-", 2)[0]);
        String normalizedSize = skuToken(size);
        String normalizedColor = skuToken(mauSac);
        if (base.isEmpty() || normalizedSize.isEmpty() || normalizedColor.isEmpty())
            return null;

        return base + "-" + normalizedSize + "-" + normalizedColor;
    }

    private String skuToken(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return normalized;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void syncImportPrice(SanPhamDTO sp) {
        if (sp == null || sp.getMaSP() == null || sp.getMaSP().isBlank())
            return;

        SanPhamDTO existing = findById(sp.getMaSP().trim());
        if (existing != null && existing.getGiaNhap() != null) {
            sp.setGiaNhap(existing.getGiaNhap());
        }
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