package com.qlgiay.dto;

public class QuyenDTO {
    private String maQuyen;
    private String tenQuyen;
    private int qlBanHang;
    private int qlKhachHang;
    private int qlSanPham;
    private int qlNhapHang;
    private int qlNhanVien;
    private int qlThongKe;
    private int qlVoucher;
    private int qlDoiTra;
    private int qlPhanQuyen;

    public QuyenDTO() {
    }

    public QuyenDTO(String maQuyen, String tenQuyen, int qlBanHang, int qlKhachHang, int qlSanPham, int qlNhapHang,
            int qlNhanVien, int qlThongKe, int qlVoucher, int qlDoiTra, int qlPhanQuyen) {
        this.maQuyen = maQuyen;
        this.tenQuyen = tenQuyen;
        this.qlBanHang = qlBanHang;
        this.qlKhachHang = qlKhachHang;
        this.qlSanPham = qlSanPham;
        this.qlNhapHang = qlNhapHang;
        this.qlNhanVien = qlNhanVien;
        this.qlThongKe = qlThongKe;
        this.qlVoucher = qlVoucher;
        this.qlDoiTra = qlDoiTra;
        this.qlPhanQuyen = qlPhanQuyen;
    }

    public String getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(String maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getTenQuyen() {
        return tenQuyen;
    }

    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    public int getQlBanHang() {
        return qlBanHang;
    }

    public void setQlBanHang(int qlBanHang) {
        this.qlBanHang = qlBanHang;
    }

    public int getQlKhachHang() {
        return qlKhachHang;
    }

    public void setQlKhachHang(int qlKhachHang) {
        this.qlKhachHang = qlKhachHang;
    }

    public int getQlSanPham() {
        return qlSanPham;
    }

    public void setQlSanPham(int qlSanPham) {
        this.qlSanPham = qlSanPham;
    }

    public int getQlNhapHang() {
        return qlNhapHang;
    }

    public void setQlNhapHang(int qlNhapHang) {
        this.qlNhapHang = qlNhapHang;
    }

    public int getQlNhanVien() {
        return qlNhanVien;
    }

    public void setQlNhanVien(int qlNhanVien) {
        this.qlNhanVien = qlNhanVien;
    }

    public int getQlThongKe() {
        return qlThongKe;
    }

    public void setQlThongKe(int qlThongKe) {
        this.qlThongKe = qlThongKe;
    }

    public int getQlVoucher() {
        return qlVoucher;
    }

    public void setQlVoucher(int qlVoucher) {
        this.qlVoucher = qlVoucher;
    }

    public int getQlDoiTra() {
        return qlDoiTra;
    }

    public void setQlDoiTra(int qlDoiTra) {
        this.qlDoiTra = qlDoiTra;
    }

    public int getQlPhanQuyen() {
        return qlPhanQuyen;
    }

    public void setQlPhanQuyen(int qlPhanQuyen) {
        this.qlPhanQuyen = qlPhanQuyen;
    }

    @Override
    public String toString() {
        return "QuyenDTO{" + "maQuyen='" + maQuyen + '\'' + ", tenQuyen='" + tenQuyen + '\'' + ", qlBanHang="
                + qlBanHang + ", qlKhachHang=" + qlKhachHang + ", qlSanPham=" + qlSanPham + ", qlNhapHang=" + qlNhapHang
                + ", qlNhanVien=" + qlNhanVien + ", qlThongKe=" + qlThongKe + ", qlVoucher=" + qlVoucher + ", qlDoiTra="
                + qlDoiTra + ", qlPhanQuyen=" + qlPhanQuyen + '}';
    }
}