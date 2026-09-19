package com.qlgiay.dto;

public class NhaCungCapDTO {
    private String maNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;
    private int trangThai;

    public NhaCungCapDTO() {}

    public NhaCungCapDTO(String maNCC, String tenNCC, String sdt, String diaChi, int trangThai) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
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

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "NhaCungCapDTO{" + "maNCC='" + maNCC + '\'' + ", tenNCC='" + tenNCC + '\'' + ", sdt='" + sdt + '\'' + ", diaChi='" + diaChi + '\'' + ", trangThai=" + trangThai + '}';
    }
}