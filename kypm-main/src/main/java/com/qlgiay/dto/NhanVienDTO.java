package com.qlgiay.dto;

import java.math.BigDecimal;

public class NhanVienDTO {
    private String maNV;
    private String ho;
    private String ten;
    private String maQuyen;
    private String taiKhoan;
    private String matKhau;
    private BigDecimal luong;
    private int trangThai;

    public NhanVienDTO() {}

    public NhanVienDTO(String maNV, String ho, String ten, String maQuyen, String taiKhoan, String matKhau, BigDecimal luong, int trangThai) {
        this.maNV = maNV;
        this.ho = ho;
        this.ten = ten;
        this.maQuyen = maQuyen;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.luong = luong;
        this.trangThai = trangThai;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(String maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public BigDecimal getLuong() {
        return luong;
    }

    public void setLuong(BigDecimal luong) {
        this.luong = luong;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "NhanVienDTO{" + "maNV='" + maNV + '\'' + ", ho='" + ho + '\'' + ", ten='" + ten + '\'' + ", maQuyen='" + maQuyen + '\'' + ", taiKhoan='" + taiKhoan + '\'' + ", luong=" + luong + ", trangThai=" + trangThai + '}';
    }
}