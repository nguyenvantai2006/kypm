package com.qlgiay.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.dto.PhieuNhapDTO;

public final class DemoTransactionData {
    private static final List<NhaCungCapDTO> SUPPLIERS = new ArrayList<>(List.of(
            new NhaCungCapDTO("TNCC001", "Công ty Giày Việt", "0911000001", "TP.HCM", 1),
            new NhaCungCapDTO("TNCC002", "Nhà phân phối Thể Thao ABC", "0911000002", "TP.HCM", 1),
            new NhaCungCapDTO("TNCC003", "Xưởng Giày Da Đông Nam", "0911000003", "Bình Dương", 1)
    ));

    private static final List<PhieuNhapDTO> IMPORTS = new ArrayList<>(List.of(
            new PhieuNhapDTO("TPN001", "DEMO001", "TNCC001", LocalDate.of(2026, 2, 1), 10, BigDecimal.valueOf(8950000)),
            new PhieuNhapDTO("TPN002", "DEMO001", "TNCC002", LocalDate.of(2026, 2, 10), 11, BigDecimal.valueOf(11050000)),
            new PhieuNhapDTO("TPN003", "DEMO001", "TNCC003", LocalDate.of(2026, 2, 20), 9, BigDecimal.valueOf(8850000))
    ));

    private static final List<List<ChiTietPhieuNhapDTO>> IMPORT_DETAILS = new ArrayList<>(List.of(
                List.of(new ChiTietPhieuNhapDTO("TPN001", "TSP001", 5, BigDecimal.valueOf(650000)),
                    new ChiTietPhieuNhapDTO("TPN001", "TSP002", 3, BigDecimal.valueOf(1250000)),
                    new ChiTietPhieuNhapDTO("TPN001", "TSP003", 2, BigDecimal.valueOf(1100000))),
                List.of(new ChiTietPhieuNhapDTO("TPN002", "TSP001", 4, BigDecimal.valueOf(650000)),
                    new ChiTietPhieuNhapDTO("TPN002", "TSP002", 5, BigDecimal.valueOf(1250000)),
                    new ChiTietPhieuNhapDTO("TPN002", "TSP003", 2, BigDecimal.valueOf(1100000))),
                List.of(new ChiTietPhieuNhapDTO("TPN003", "TSP001", 3, BigDecimal.valueOf(650000)),
                    new ChiTietPhieuNhapDTO("TPN003", "TSP002", 2, BigDecimal.valueOf(1250000)),
                    new ChiTietPhieuNhapDTO("TPN003", "TSP003", 4, BigDecimal.valueOf(1100000)))
    ));

    private static final List<HoaDonDTO> INVOICES = new ArrayList<>(List.of(
            new HoaDonDTO("THD001", "DEMO001", null, null, LocalDate.of(2026, 3, 1), BigDecimal.valueOf(3550000)),
            new HoaDonDTO("THD002", "DEMO001", null, null, LocalDate.of(2026, 3, 5), BigDecimal.valueOf(2100000)),
            new HoaDonDTO("THD003", "DEMO001", null, null, LocalDate.of(2026, 3, 10), BigDecimal.valueOf(2300000))
    ));

    private static final List<List<ChiTietHoaDonDTO>> INVOICE_DETAILS = new ArrayList<>(List.of(
            List.of(new ChiTietHoaDonDTO("THD001", "TSP001", 1, BigDecimal.valueOf(850000)),
                    new ChiTietHoaDonDTO("THD001", "TSP002", 1, BigDecimal.valueOf(1250000)),
                    new ChiTietHoaDonDTO("THD001", "TSP003", 1, BigDecimal.valueOf(1450000))),
            List.of(new ChiTietHoaDonDTO("THD002", "TSP001", 1, BigDecimal.valueOf(850000)),
                    new ChiTietHoaDonDTO("THD002", "TSP002", 1, BigDecimal.valueOf(1250000))),
            List.of(new ChiTietHoaDonDTO("THD003", "TSP001", 1, BigDecimal.valueOf(850000)),
                    new ChiTietHoaDonDTO("THD003", "TSP003", 1, BigDecimal.valueOf(1450000)))
    ));

    private DemoTransactionData() {
    }

    public static synchronized List<NhaCungCapDTO> suppliers(boolean activeOnly) {
        return SUPPLIERS.stream().filter(s -> !activeOnly || s.getTrangThai() == 1).map(DemoTransactionData::copy).toList();
    }

    public static synchronized NhaCungCapDTO findSupplier(String maNCC) {
        return SUPPLIERS.stream().filter(s -> s.getMaNCC().equalsIgnoreCase(maNCC)).findFirst().map(DemoTransactionData::copy).orElse(null);
    }

    public static synchronized boolean addSupplier(NhaCungCapDTO supplier) {
        if (findSupplier(supplier.getMaNCC()) != null) return false;
        SUPPLIERS.add(copy(supplier));
        return true;
    }

    public static synchronized boolean updateSupplier(NhaCungCapDTO supplier) {
        for (int i = 0; i < SUPPLIERS.size(); i++) {
            if (SUPPLIERS.get(i).getMaNCC().equalsIgnoreCase(supplier.getMaNCC())) {
                SUPPLIERS.set(i, copy(supplier));
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean setSupplierStatus(String maNCC, int status) {
        NhaCungCapDTO supplier = SUPPLIERS.stream().filter(s -> s.getMaNCC().equalsIgnoreCase(maNCC)).findFirst().orElse(null);
        if (supplier == null) return false;
        supplier.setTrangThai(status);
        return true;
    }

    public static synchronized List<NhaCungCapDTO> searchSuppliers(String keyword, boolean activeOnly) {
        String value = keyword == null ? "" : keyword.trim().toLowerCase();
        return suppliers(activeOnly).stream()
                .filter(s -> value.isEmpty() || s.getMaNCC().toLowerCase().contains(value) || s.getTenNCC().toLowerCase().contains(value))
                .sorted(Comparator.comparing(NhaCungCapDTO::getTenNCC)).toList();
    }

    public static synchronized List<PhieuNhapDTO> imports() {
        return IMPORTS.stream().map(DemoTransactionData::copy).toList();
    }

    public static synchronized List<ChiTietPhieuNhapDTO> importDetails(String maPN) {
        for (int i = 0; i < IMPORTS.size(); i++) if (IMPORTS.get(i).getMaPN().equalsIgnoreCase(maPN)) return IMPORT_DETAILS.get(i).stream().map(DemoTransactionData::copy).toList();
        return List.of();
    }

    public static synchronized List<HoaDonDTO> invoices() {
        return INVOICES.stream().map(DemoTransactionData::copy).toList();
    }

    public static synchronized HoaDonDTO findInvoice(String maHD) {
        return INVOICES.stream().filter(h -> h.getMaHD().equalsIgnoreCase(maHD)).findFirst().map(DemoTransactionData::copy).orElse(null);
    }

    public static synchronized List<ChiTietHoaDonDTO> invoiceDetails(String maHD) {
        for (int i = 0; i < INVOICES.size(); i++) if (INVOICES.get(i).getMaHD().equalsIgnoreCase(maHD)) return INVOICE_DETAILS.get(i).stream().map(DemoTransactionData::copy).toList();
        return List.of();
    }

    public static synchronized boolean addImport(PhieuNhapDTO receipt, List<ChiTietPhieuNhapDTO> details) {
        if (IMPORTS.stream().anyMatch(p -> p.getMaPN().equalsIgnoreCase(receipt.getMaPN()))) return false;
        for (ChiTietPhieuNhapDTO detail : details) {
            if (DemoProductData.findById(detail.getMaSP()) == null
                || !DemoProductData.changeStock(detail.getMaSP(), detail.getSoLuong())
                || !DemoProductData.setImportPrice(detail.getMaSP(), detail.getGiaNhap())) return false;
        }
        IMPORTS.add(copy(receipt));
        IMPORT_DETAILS.add(details.stream().map(DemoTransactionData::copy).toList());
        return true;
    }

    public static synchronized boolean addInvoice(HoaDonDTO invoice, List<ChiTietHoaDonDTO> details) {
        if (INVOICES.stream().anyMatch(h -> h.getMaHD().equalsIgnoreCase(invoice.getMaHD()))) return false;
        for (ChiTietHoaDonDTO detail : details) {
            var product = DemoProductData.findById(detail.getMaSP());
            if (product == null || detail.getSoLuong() <= 0 || product.getSoLuong() < detail.getSoLuong()) return false;
        }
        for (ChiTietHoaDonDTO detail : details) DemoProductData.changeStock(detail.getMaSP(), -detail.getSoLuong());
        INVOICES.add(copy(invoice));
        INVOICE_DETAILS.add(details.stream().map(DemoTransactionData::copy).toList());
        return true;
    }

    private static NhaCungCapDTO copy(NhaCungCapDTO s) { return new NhaCungCapDTO(s.getMaNCC(), s.getTenNCC(), s.getSdt(), s.getDiaChi(), s.getTrangThai()); }
    private static PhieuNhapDTO copy(PhieuNhapDTO p) { return new PhieuNhapDTO(p.getMaPN(), p.getMaNV(), p.getMaNCC(), p.getNgayNhap(), p.getTongSoMatHang(), p.getTongTien()); }
    private static ChiTietPhieuNhapDTO copy(ChiTietPhieuNhapDTO c) { return new ChiTietPhieuNhapDTO(c.getMaPN(), c.getMaSP(), c.getSoLuong(), c.getGiaNhap()); }
    private static HoaDonDTO copy(HoaDonDTO h) { return new HoaDonDTO(h.getMaHD(), h.getMaNV(), h.getMaKH(), h.getMaVoucher(), h.getNgayLap(), h.getTongTien()); }
    private static ChiTietHoaDonDTO copy(ChiTietHoaDonDTO c) { return new ChiTietHoaDonDTO(c.getMaHD(), c.getMaSP(), c.getSoLuong(), c.getDonGia()); }
}