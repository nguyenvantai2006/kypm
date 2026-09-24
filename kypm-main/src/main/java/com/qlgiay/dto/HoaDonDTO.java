package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HoaDonDTO {
    private String maHD;
    private String maNV;
    private String maKH;
    private String maVoucher;
    private LocalDate ngayLap;
    private BigDecimal tongTien;

    public HoaDonDTO() {}

    public HoaDonDTO(String maHD, String maNV, String maKH, String maVoucher, LocalDate ngayLap, BigDecimal tongTien) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.maKH = maKH;
        this.maVoucher = maVoucher;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    @Override
    public String toString() {
        return "HoaDonDTO{" + "maHD='" + maHD + '\'' + ", maNV='" + maNV + '\'' + ", maKH='" + maKH + '\'' + ", maVoucher='" + maVoucher + '\'' + ", ngayLap=" + ngayLap + ", tongTien=" + tongTien + '}';
    }
}