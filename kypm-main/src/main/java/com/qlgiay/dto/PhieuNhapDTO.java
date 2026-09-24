package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PhieuNhapDTO {
    private String maPN;
    private String maNV;
    private String maNCC;
    private LocalDate ngayNhap;
    private int tongSoMatHang;
    private BigDecimal tongTien;

    public PhieuNhapDTO() {}

    public PhieuNhapDTO(String maPN, String maNV, String maNCC, LocalDate ngayNhap, int tongSoMatHang, BigDecimal tongTien) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.ngayNhap = ngayNhap;
        this.tongSoMatHang = tongSoMatHang;
        this.tongTien = tongTien;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public int getTongSoMatHang() {
        return tongSoMatHang;
    }

    public void setTongSoMatHang(int tongSoMatHang) {
        this.tongSoMatHang = tongSoMatHang;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    @Override
    public String toString() {
        return "PhieuNhapDTO{" + "maPN='" + maPN + '\'' + ", maNV='" + maNV + '\'' + ", maNCC='" + maNCC + '\'' + ", ngayNhap=" + ngayNhap + ", tongSoMatHang=" + tongSoMatHang + ", tongTien=" + tongTien + '}';
    }
}