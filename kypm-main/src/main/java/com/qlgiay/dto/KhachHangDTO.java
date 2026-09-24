package com.qlgiay.dto;

public class KhachHangDTO {
    private String maKH;
    private String tenKH;
    private String sdt;
    private String diaChi;
    private int diemTichLuy;
    private int trangThai;

    public KhachHangDTO() {}

    public KhachHangDTO(String maKH, String tenKH, String sdt, String diaChi, int diemTichLuy, int trangThai) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
        this.trangThai = trangThai;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "KhachHangDTO{" + "maKH='" + maKH + '\'' + ", tenKH='" + tenKH + '\'' + ", sdt='" + sdt + '\'' + ", diaChi='" + diaChi + '\'' + ", diemTichLuy=" + diemTichLuy + ", trangThai=" + trangThai + '}';
    }
}