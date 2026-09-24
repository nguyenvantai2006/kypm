package com.qlgiay.dto;

import java.math.BigDecimal;

public class ChiTietPhieuNhapDTO {
    private String maPN;
    private String maSP;
    private int soLuong;
    private BigDecimal giaNhap;

    public ChiTietPhieuNhapDTO() {}

    public ChiTietPhieuNhapDTO(String maPN, String maSP, int soLuong, BigDecimal giaNhap) {
        this.maPN = maPN;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
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

    public BigDecimal getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhapDTO{" + "maPN='" + maPN + '\'' + ", maSP='" + maSP + '\'' + ", soLuong=" + soLuong + ", giaNhap=" + giaNhap + '}';
    }
}