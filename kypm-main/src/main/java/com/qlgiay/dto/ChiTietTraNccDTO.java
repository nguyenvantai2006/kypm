package com.qlgiay.dto;

import java.math.BigDecimal;

public class ChiTietTraNccDTO {
    private String maPT;
    private String maSP;
    private int soLuong;
    private BigDecimal giaNhap;

    public ChiTietTraNccDTO() {}

    public ChiTietTraNccDTO(String maPT, String maSP, int soLuong, BigDecimal giaNhap) {
        this.maPT = maPT;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
    }

    public String getMaPT() { return maPT; }
    public void setMaPT(String maPT) { this.maPT = maPT; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }
}
