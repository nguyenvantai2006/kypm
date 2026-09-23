package com.qlgiay.bus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.qlgiay.dao.ThongKeDAO;
import com.qlgiay.dto.DoanhThuTheoNgayDTO;
import com.qlgiay.dto.LoaiSanPhamThongKeDTO;
import com.qlgiay.dto.LoiNhuanTheoKyDTO;
import com.qlgiay.dto.TopKhachHangDTO;
import com.qlgiay.dto.TopSanPhamDTO;

public class ThongKeBUS {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public BigDecimal getTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTongDoanhThu(tuNgay, denNgay);
    }

    public int getSoHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getSoHoaDon(tuNgay, denNgay);
    }

    public BigDecimal getTongTienVon(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getTongTienVon(tuNgay, denNgay);
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

    public List<LoiNhuanTheoKyDTO> getLoiNhuanTheoThang(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getLoiNhuanTheoThang(tuNgay, denNgay);
    }

    public List<LoiNhuanTheoKyDTO> getLoiNhuanTheoQuy(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.getLoiNhuanTheoQuy(tuNgay, denNgay);
    }
}