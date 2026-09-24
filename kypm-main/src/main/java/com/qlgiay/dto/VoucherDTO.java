package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VoucherDTO {
    private String maVoucher;
    private String tenVoucher;
    private int phanTramGiam;
    private BigDecimal soTienGiam;
    private BigDecimal giamToiDa;
    private BigDecimal dieuKienApDung;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private int soLuong;
    private int trangThai;

    public VoucherDTO() {}

    public VoucherDTO(String maVoucher, String tenVoucher, int phanTramGiam, BigDecimal soTienGiam, BigDecimal giamToiDa, BigDecimal dieuKienApDung, LocalDate ngayBatDau, LocalDate ngayKetThuc, int soLuong, int trangThai) {
        this.maVoucher = maVoucher;
        this.tenVoucher = tenVoucher;
        this.phanTramGiam = phanTramGiam;
        this.soTienGiam = soTienGiam;
        this.giamToiDa = giamToiDa;
        this.dieuKienApDung = dieuKienApDung;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public String getTenVoucher() {
        return tenVoucher;
    }

    public void setTenVoucher(String tenVoucher) {
        this.tenVoucher = tenVoucher;
    }

    public int getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(int phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }

    public BigDecimal getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(BigDecimal soTienGiam) {
        this.soTienGiam = soTienGiam;
    }

    public BigDecimal getGiamToiDa() {
        return giamToiDa;
    }

    public void setGiamToiDa(BigDecimal giamToiDa) {
        this.giamToiDa = giamToiDa;
    }

    public BigDecimal getDieuKienApDung() {
        return dieuKienApDung;
    }

    public void setDieuKienApDung(BigDecimal dieuKienApDung) {
        this.dieuKienApDung = dieuKienApDung;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "VoucherDTO{" + "maVoucher='" + maVoucher + '\'' + ", tenVoucher='" + tenVoucher + '\'' + ", phanTramGiam=" + phanTramGiam + ", soTienGiam=" + soTienGiam + ", giamToiDa=" + giamToiDa + ", dieuKienApDung=" + dieuKienApDung + ", ngayBatDau=" + ngayBatDau + ", ngayKetThuc=" + ngayKetThuc + ", soLuong=" + soLuong + ", trangThai=" + trangThai + '}';
    }
}