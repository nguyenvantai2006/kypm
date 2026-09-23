package com.qlgiay.bus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.qlgiay.dao.ThongKeDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.DoanhThuTheoNgayDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.LoaiSanPhamThongKeDTO;
import com.qlgiay.dto.LoiNhuanTheoKyDTO;
import com.qlgiay.dto.PhieuNhapDTO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.dto.TopKhachHangDTO;
import com.qlgiay.dto.TopSanPhamDTO;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoProductData;
import com.qlgiay.util.DemoTransactionData;

public class ThongKeBUS {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public BigDecimal getTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return DemoTransactionData.invoices().stream()
                    .filter(invoice -> inRange(invoice.getNgayLap(), tuNgay, denNgay))
                    .map(HoaDonDTO::getTongTien)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return thongKeDAO.getTongDoanhThu(tuNgay, denNgay);
    }

    public int getSoHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return (int) DemoTransactionData.invoices().stream()
                    .filter(invoice -> inRange(invoice.getNgayLap(), tuNgay, denNgay))
                    .count();
        }
        return thongKeDAO.getSoHoaDon(tuNgay, denNgay);
    }

    public BigDecimal getTongTienVon(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return DemoTransactionData.imports().stream()
                    .filter(receipt -> inRange(receipt.getNgayNhap(), tuNgay, denNgay))
                    .map(PhieuNhapDTO::getTongTien)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return thongKeDAO.getTongTienVon(tuNgay, denNgay);
    }

    public int getTongSanPhamBan(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return getDemoInvoiceDetails(tuNgay, denNgay).stream()
                    .mapToInt(ChiTietHoaDonDTO::getSoLuong)
                    .sum();
        }
        return thongKeDAO.getTongSanPhamBan(tuNgay, denNgay);
    }

    public int getSoKhachMua(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return (int) DemoTransactionData.invoices().stream()
                    .filter(invoice -> inRange(invoice.getNgayLap(), tuNgay, denNgay))
                    .map(HoaDonDTO::getMaKH)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
        }
        return thongKeDAO.getSoKhachMua(tuNgay, denNgay);
    }

    public List<DoanhThuTheoNgayDTO> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            Map<LocalDate, BigDecimal> revenueByDate = new java.util.TreeMap<>();
            DemoTransactionData.invoices().stream()
                    .filter(invoice -> inRange(invoice.getNgayLap(), tuNgay, denNgay))
                    .forEach(invoice -> revenueByDate.merge(invoice.getNgayLap(), invoice.getTongTien(), BigDecimal::add));

            List<DoanhThuTheoNgayDTO> result = new ArrayList<>();
            revenueByDate.forEach((date, revenue) -> {
                DoanhThuTheoNgayDTO dto = new DoanhThuTheoNgayDTO();
                dto.setNgay(date);
                dto.setDoanhThu(revenue);
                result.add(dto);
            });
            return result;
        }
        return thongKeDAO.getDoanhThuTheoNgay(tuNgay, denNgay);
    }

    public List<TopSanPhamDTO> getTopSanPham(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            Map<String, ProductStats> statsByProduct = new HashMap<>();
            for (ChiTietHoaDonDTO detail : getDemoInvoiceDetails(tuNgay, denNgay)) {
                ProductStats stats = statsByProduct.computeIfAbsent(detail.getMaSP(), ignored -> new ProductStats());
                stats.quantity += detail.getSoLuong();
                stats.revenue = stats.revenue.add(detail.getDonGia().multiply(BigDecimal.valueOf(detail.getSoLuong())));
            }

            return statsByProduct.entrySet().stream()
                    .map(entry -> {
                        SanPhamDTO product = DemoProductData.findById(entry.getKey());
                        ProductStats stats = entry.getValue();
                        TopSanPhamDTO dto = new TopSanPhamDTO();
                        dto.setMaSP(entry.getKey());
                        dto.setTenSP(product == null ? entry.getKey() : product.getTenSP());
                        dto.setSoLuongBan(stats.quantity);
                        dto.setDoanhThu(stats.revenue);
                        return dto;
                    })
                    .sorted(Comparator.comparingInt(TopSanPhamDTO::getSoLuongBan).reversed()
                            .thenComparing(TopSanPhamDTO::getDoanhThu, Comparator.reverseOrder()))
                    .limit(5)
                    .toList();
        }
        return thongKeDAO.getTopSanPham(tuNgay, denNgay);
    }

    public List<TopKhachHangDTO> getTopKhachHang(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTopKhachHang(tuNgay, denNgay);
    }

    public List<LoaiSanPhamThongKeDTO> getThongKeTheoLoai(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getThongKeTheoLoai(tuNgay, denNgay);
    }

    public List<LoiNhuanTheoKyDTO> getLoiNhuanTheoThang(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return getDemoProfitByPeriod(tuNgay, denNgay, false);
        }
        return thongKeDAO.getLoiNhuanTheoThang(tuNgay, denNgay);
    }

    public List<LoiNhuanTheoKyDTO> getLoiNhuanTheoQuy(LocalDate tuNgay, LocalDate denNgay) {
        if (DBConnect.isDemoMode()) {
            return getDemoProfitByPeriod(tuNgay, denNgay, true);
        }
        return thongKeDAO.getLoiNhuanTheoQuy(tuNgay, denNgay);
    }

    private List<ChiTietHoaDonDTO> getDemoInvoiceDetails(LocalDate tuNgay, LocalDate denNgay) {
        List<ChiTietHoaDonDTO> details = new ArrayList<>();
        for (HoaDonDTO invoice : DemoTransactionData.invoices()) {
            if (inRange(invoice.getNgayLap(), tuNgay, denNgay)) {
                details.addAll(DemoTransactionData.invoiceDetails(invoice.getMaHD()));
            }
        }
        return details;
    }

    private List<LoiNhuanTheoKyDTO> getDemoProfitByPeriod(LocalDate tuNgay, LocalDate denNgay, boolean quarterly) {
        Map<Integer, LoiNhuanTheoKyDTO> result = new java.util.TreeMap<>();
        for (HoaDonDTO invoice : DemoTransactionData.invoices()) {
            if (!inRange(invoice.getNgayLap(), tuNgay, denNgay)) continue;
            int period = quarterly ? (invoice.getNgayLap().getMonthValue() - 1) / 3 + 1 : invoice.getNgayLap().getMonthValue();
            int key = invoice.getNgayLap().getYear() * 100 + period;
            LoiNhuanTheoKyDTO dto = result.computeIfAbsent(key, ignored -> createPeriod(invoice.getNgayLap().getYear(), period));
            dto.setDoanhThu(dto.getDoanhThu().add(invoice.getTongTien()));
        }
        for (PhieuNhapDTO receipt : DemoTransactionData.imports()) {
            if (!inRange(receipt.getNgayNhap(), tuNgay, denNgay)) continue;
            int period = quarterly ? (receipt.getNgayNhap().getMonthValue() - 1) / 3 + 1 : receipt.getNgayNhap().getMonthValue();
            int key = receipt.getNgayNhap().getYear() * 100 + period;
            LoiNhuanTheoKyDTO dto = result.computeIfAbsent(key, ignored -> createPeriod(receipt.getNgayNhap().getYear(), period));
            dto.setTienVon(dto.getTienVon().add(receipt.getTongTien()));
        }
        return new ArrayList<>(result.values());
    }

    private LoiNhuanTheoKyDTO createPeriod(int year, int period) {
        LoiNhuanTheoKyDTO dto = new LoiNhuanTheoKyDTO();
        dto.setNam(year);
        dto.setKy(period);
        return dto;
    }

    private boolean inRange(LocalDate date, LocalDate from, LocalDate to) {
        return date != null && !date.isBefore(from) && !date.isAfter(to);
    }

    private static class ProductStats {
        private int quantity;
        private BigDecimal revenue = BigDecimal.ZERO;
    }
}