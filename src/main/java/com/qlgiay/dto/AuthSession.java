package com.qlgiay.dto;

public class AuthSession {
    private NhanVienDTO nhanVien;
    private QuyenDTO quyen;

    public AuthSession() {}

    public AuthSession(NhanVienDTO nhanVien, QuyenDTO quyen) {
        this.nhanVien = nhanVien;
        this.quyen = quyen;
    }

    public NhanVienDTO getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVienDTO nhanVien) {
        this.nhanVien = nhanVien;
    }

    public QuyenDTO getQuyen() {
        return quyen;
    }

    public void setQuyen(QuyenDTO quyen) {
        this.quyen = quyen;
    }

    @Override
    public String toString() {
        return "AuthSession{" +
                "nhanVien=" + nhanVien +
                ", quyen=" + quyen +
                '}';
    }
}