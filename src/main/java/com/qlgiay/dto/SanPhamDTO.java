package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SanPhamDTO {
    private String maSP;
    private String tenSP;
    private String loaiSP;
    private String donViTinh;
    private int soLuong;
    private BigDecimal donGia;
    private String mauSac;
    private String size;
    private String chatLieu;
    private String thuongHieu;
    private String nuocSanXuat;
    private LocalDate ngaySanXuat;
    private String moTa;
    private String hinhAnh;
    private String maNCC;
    private int trangThai;

    public SanPhamDTO() {}

    public SanPhamDTO(String maSP, String tenSP, String loaiSP, String donViTinh, int soLuong, BigDecimal donGia, String mauSac, String size, String chatLieu, String thuongHieu, String nuocSanXuat, LocalDate ngaySanXuat, String moTa, String hinhAnh, int trangThai) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.mauSac = mauSac;
        this.size = size;
        this.chatLieu = chatLieu;
        this.thuongHieu = thuongHieu;
        this.nuocSanXuat = nuocSanXuat;
        this.ngaySanXuat = ngaySanXuat;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getLoaiSP() {
        return loaiSP;
    }

    public void setLoaiSP(String loaiSP) {
        this.loaiSP = loaiSP;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
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

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(String chatLieu) {
        this.chatLieu = chatLieu;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getNuocSanXuat() {
        return nuocSanXuat;
    }

    public void setNuocSanXuat(String nuocSanXuat) {
        this.nuocSanXuat = nuocSanXuat;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "SanPhamDTO{" + "maSP='" + maSP + '\'' + ", tenSP='" + tenSP + '\'' + ", loaiSP='" + loaiSP + '\'' + ", donViTinh='" + donViTinh + '\'' + ", soLuong=" + soLuong + ", donGia=" + donGia + ", mauSac='" + mauSac + '\'' + ", size='" + size + '\'' + ", chatLieu='" + chatLieu + '\'' + ", thuongHieu='" + thuongHieu + '\'' + ", nuocSanXuat='" + nuocSanXuat + '\'' + ", ngaySanXuat=" + ngaySanXuat + ", moTa='" + moTa + '\'' + ", hinhAnh='" + hinhAnh + '\'' + ", trangThai=" + trangThai + '}';
    }
}