package com.qlgiay.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.qlgiay.dto.SanPhamDTO;

public final class DemoProductData {
    private static final List<SanPhamDTO> PRODUCTS = new ArrayList<>(List.of(
            new SanPhamDTO("TSP001", "Giày Sneaker Urban", "Giày Sneaker", "Đôi", 56,
                    BigDecimal.valueOf(850000), "Trắng", "42", "Vải Mesh", "Urban", "Việt Nam",
                    LocalDate.of(2026, 1, 10), "Mẫu thử bán hàng", "", 1),
            new SanPhamDTO("TSP002", "Giày Chạy Bộ Speed", "Giày Chạy Bộ", "Đôi", 42,
                    BigDecimal.valueOf(1250000), "Đen", "41", "Vải Knit", "Speed", "Việt Nam",
                    LocalDate.of(2026, 1, 12), "Mẫu thử chạy bộ", "", 1),
            new SanPhamDTO("TSP003", "Giày Tây Classic", "Giày Tây", "Đôi", 31,
                    BigDecimal.valueOf(1450000), "Nâu", "40", "Da bò", "Classic", "Việt Nam",
                    LocalDate.of(2025, 12, 20), "Mẫu thử công sở", "", 1)
    ));

    static {
        PRODUCTS.get(0).setGiaNhap(BigDecimal.valueOf(650000));
        PRODUCTS.get(1).setGiaNhap(BigDecimal.valueOf(1250000));
        PRODUCTS.get(2).setGiaNhap(BigDecimal.valueOf(1100000));
        PRODUCTS.forEach(product -> {
            product.setPhanTramLoiNhuan(BigDecimal.valueOf(20));
            product.setDonGia(SanPhamDTO.tinhGiaBan(product.getGiaNhap(), product.getPhanTramLoiNhuan()));
        });
    }

    private DemoProductData() {
    }

    public static synchronized List<SanPhamDTO> all() {
        return PRODUCTS.stream().map(DemoProductData::copy).toList();
    }

    public static synchronized SanPhamDTO findById(String maSP) {
        return PRODUCTS.stream()
                .filter(sp -> sp.getMaSP().equalsIgnoreCase(maSP))
                .findFirst()
                .map(DemoProductData::copy)
                .orElse(null);
    }

    public static synchronized boolean insert(SanPhamDTO product) {
        if (findById(product.getMaSP()) != null) return false;
        PRODUCTS.add(copy(product));
        return true;
    }

    public static synchronized boolean update(SanPhamDTO product) {
        for (int i = 0; i < PRODUCTS.size(); i++) {
            if (PRODUCTS.get(i).getMaSP().equalsIgnoreCase(product.getMaSP())) {
                PRODUCTS.set(i, copy(product));
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean setStatus(String maSP, int status) {
        SanPhamDTO product = findMutableById(maSP);
        if (product == null) return false;
        product.setTrangThai(status);
        return true;
    }

    public static synchronized boolean changeStock(String maSP, int delta) {
        SanPhamDTO product = findMutableById(maSP);
        if (product == null || product.getSoLuong() + delta < 0) return false;
        product.setSoLuong(product.getSoLuong() + delta);
        return true;
    }

    public static synchronized boolean setImportPrice(String maSP, BigDecimal giaNhap) {
        SanPhamDTO product = findMutableById(maSP);
        if (product == null || giaNhap == null || giaNhap.compareTo(BigDecimal.ZERO) <= 0) return false;
        product.setGiaNhap(giaNhap);
        product.setPhanTramLoiNhuan(BigDecimal.valueOf(20));
        product.setDonGia(SanPhamDTO.tinhGiaBan(giaNhap, product.getPhanTramLoiNhuan()));
        return true;
    }

    public static synchronized List<SanPhamDTO> search(String keyword, boolean activeOnly) {
        String value = keyword == null ? "" : keyword.trim().toLowerCase();
        return PRODUCTS.stream()
                .filter(sp -> !activeOnly || sp.getTrangThai() == 1)
                .filter(sp -> value.isEmpty()
                        || sp.getMaSP().toLowerCase().contains(value)
                        || sp.getTenSP().toLowerCase().contains(value)
                        || sp.getLoaiSP().toLowerCase().contains(value)
                        || sp.getThuongHieu().toLowerCase().contains(value))
                .sorted(Comparator.comparing(SanPhamDTO::getTenSP))
                .map(DemoProductData::copy)
                .toList();
    }

    private static SanPhamDTO findMutableById(String maSP) {
        return PRODUCTS.stream()
                .filter(sp -> sp.getMaSP().equalsIgnoreCase(maSP))
                .findFirst()
                .orElse(null);
    }

    private static SanPhamDTO copy(SanPhamDTO source) {
        SanPhamDTO copy = new SanPhamDTO(source.getMaSP(), source.getTenSP(), source.getLoaiSP(), source.getDonViTinh(),
                source.getSoLuong(), source.getDonGia(), source.getMauSac(), source.getSize(), source.getChatLieu(),
                source.getThuongHieu(), source.getNuocSanXuat(), source.getNgaySanXuat(), source.getMoTa(),
                source.getHinhAnh(), source.getTrangThai());
        copy.setGiaNhap(source.getGiaNhap());
        copy.setPhanTramLoiNhuan(source.getPhanTramLoiNhuan());
        copy.setGiaKhuyenMai(source.getGiaKhuyenMai());
        return copy;
    }
}