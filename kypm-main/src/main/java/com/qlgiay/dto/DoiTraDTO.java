package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DoiTraDTO {
    private String maDT;
    private String maHD;
    private String maNV;
    private String maSP;
    private LocalDate ngayDoiTra;
    private int soLuong;
    private BigDecimal tongTienHoan;
    private String lyDo;
    private String tinhTrang;

    public DoiTraDTO() {}

    public DoiTraDTO(String maDT, String maHD, String maNV, String maSP, LocalDate ngayDoiTra, int soLuong, BigDecimal tongTienHoan, String lyDo, String tinhTrang) {
        this.maDT = maDT;
        this.maHD = maHD;
        this.maNV = maNV;
        this.maSP = maSP;
        this.ngayDoiTra = ngayDoiTra;
        this.soLuong = soLuong;
        this.tongTienHoan = tongTienHoan;
        this.lyDo = lyDo;
        this.tinhTrang = tinhTrang;
    }

    public String getMaDT() {
        return maDT;
    }

    public void setMaDT(String maDT) {
        this.maDT = maDT;
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

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public LocalDate getNgayDoiTra() {
        return ngayDoiTra;
    }

    public void setNgayDoiTra(LocalDate ngayDoiTra) {
        this.ngayDoiTra = ngayDoiTra;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getTongTienHoan() {
        return tongTienHoan;
    }

    public void setTongTienHoan(BigDecimal tongTienHoan) {
        this.tongTienHoan = tongTienHoan;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    @Override
    public String toString() {
        return "DoiTraDTO{" + "maDT='" + maDT + '\'' + ", maHD='" + maHD + '\'' + ", maNV='" + maNV + '\'' + ", maSP='" + maSP + '\'' + ", ngayDoiTra=" + ngayDoiTra + ", soLuong=" + soLuong + ", tongTienHoan=" + tongTienHoan + ", lyDo='" + lyDo + '\'' + ", tinhTrang='" + tinhTrang + '\'' + '}';
    }
}