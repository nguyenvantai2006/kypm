package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PhieuTraNccDTO {
    private String maPT;
    private String maPN;
    private String maNV;
    private String maNCC;
    private LocalDate ngayTao;
    private int tongSoMatHang;
    private BigDecimal tongTien;
    private String lyDo;
    private String trangThai;
    private LocalDate ngayXuLy;
    private String nguoiXuLy;

    public String getMaPT() { return maPT; }
    public void setMaPT(String maPT) { this.maPT = maPT; }
    public String getMaPN() { return maPN; }
    public void setMaPN(String maPN) { this.maPN = maPN; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public LocalDate getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDate ngayTao) { this.ngayTao = ngayTao; }
    public int getTongSoMatHang() { return tongSoMatHang; }
    public void setTongSoMatHang(int tongSoMatHang) { this.tongSoMatHang = tongSoMatHang; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public LocalDate getNgayXuLy() { return ngayXuLy; }
    public void setNgayXuLy(LocalDate ngayXuLy) { this.ngayXuLy = ngayXuLy; }
    public String getNguoiXuLy() { return nguoiXuLy; }
    public void setNguoiXuLy(String nguoiXuLy) { this.nguoiXuLy = nguoiXuLy; }
}
