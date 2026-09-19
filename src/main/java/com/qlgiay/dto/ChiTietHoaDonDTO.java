package com.qlgiay.dto;

import java.math.BigDecimal;

public class ChiTietHoaDonDTO {
    private String maHD;
    private String maSP;
    private int soLuong;
    private BigDecimal donGia;

    public ChiTietHoaDonDTO() {}

    public ChiTietHoaDonDTO(String maHD, String maSP, int soLuong, BigDecimal donGia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDonDTO{" + "maHD='" + maHD + '\'' + ", maSP='" + maSP + '\'' + ", soLuong=" + soLuong + ", donGia=" + donGia + '}';
    }
}