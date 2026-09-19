package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PhieuBaoHanhDTO {
    private String maPBH;
    private String maHD;
    private String maSP;
    private String maKH;
    private LocalDate ngayNhan;
    private LocalDate ngayTraDuKien;
    private String loiCanBaoHanh;
    private BigDecimal chiPhiPhatSinh;
    private int trangThai;

    public PhieuBaoHanhDTO() {}

    public PhieuBaoHanhDTO(String maPBH, String maHD, String maSP, String maKH, LocalDate ngayNhan, LocalDate ngayTraDuKien, String loiCanBaoHanh, BigDecimal chiPhiPhatSinh, int trangThai) {
        this.maPBH = maPBH;
        this.maHD = maHD;
        this.maSP = maSP;
        this.maKH = maKH;
        this.ngayNhan = ngayNhan;
        this.ngayTraDuKien = ngayTraDuKien;
        this.loiCanBaoHanh = loiCanBaoHanh;
        this.chiPhiPhatSinh = chiPhiPhatSinh;
        this.trangThai = trangThai;
    }

    public String getMaPBH() {
        return maPBH;
    }

    public void setMaPBH(String maPBH) {
        this.maPBH = maPBH;
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

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public LocalDate getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(LocalDate ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public LocalDate getNgayTraDuKien() {
        return ngayTraDuKien;
    }

    public void setNgayTraDuKien(LocalDate ngayTraDuKien) {
        this.ngayTraDuKien = ngayTraDuKien;
    }

    public String getLoiCanBaoHanh() {
        return loiCanBaoHanh;
    }

    public void setLoiCanBaoHanh(String loiCanBaoHanh) {
        this.loiCanBaoHanh = loiCanBaoHanh;
    }

    public BigDecimal getChiPhiPhatSinh() {
        return chiPhiPhatSinh;
    }

    public void setChiPhiPhatSinh(BigDecimal chiPhiPhatSinh) {
        this.chiPhiPhatSinh = chiPhiPhatSinh;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "PhieuBaoHanhDTO{" + "maPBH='" + maPBH + '\'' + ", maHD='" + maHD + '\'' + ", maSP='" + maSP + '\'' + ", maKH='" + maKH + '\'' + ", ngayNhan=" + ngayNhan + ", ngayTraDuKien=" + ngayTraDuKien + ", loiCanBaoHanh='" + loiCanBaoHanh + '\'' + ", chiPhiPhatSinh=" + chiPhiPhatSinh + ", trangThai=" + trangThai + '}';
    }
}