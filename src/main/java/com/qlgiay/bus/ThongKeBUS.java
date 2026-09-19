package com.qlgiay.bus;

import com.qlgiay.dao.ThongKeDAO;
import com.qlgiay.dto.DoanhThuTheoNgayDTO;
import com.qlgiay.dto.TopKhachHangDTO;
import com.qlgiay.dto.TopSanPhamDTO;
import com.qlgiay.dto.LoaiSanPhamThongKeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ThongKeBUS {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public BigDecimal getTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTongDoanhThu(tuNgay, denNgay);
    }

    public int getSoHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getSoHoaDon(tuNgay, denNgay);
    }

    public int getTongSanPhamBan(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTongSanPhamBan(tuNgay, denNgay);
    }

    public int getSoKhachMua(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getSoKhachMua(tuNgay, denNgay);
    }

    public List<DoanhThuTheoNgayDTO> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getDoanhThuTheoNgay(tuNgay, denNgay);
    }

    public List<TopSanPhamDTO> getTopSanPham(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTopSanPham(tuNgay, denNgay);
    }

    public List<TopKhachHangDTO> getTopKhachHang(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTopKhachHang(tuNgay, denNgay);
    }

    public List<LoaiSanPhamThongKeDTO> getThongKeTheoLoai(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getThongKeTheoLoai(tuNgay, denNgay);
    }
}