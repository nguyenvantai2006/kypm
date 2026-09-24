package com.qlgiay.dto;

import java.math.BigDecimal;

public class LoiNhuanTheoKyDTO {
    private int nam;
    private int ky;
    private BigDecimal doanhThu = BigDecimal.ZERO;
    private BigDecimal tienVon = BigDecimal.ZERO;

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public int getKy() {
        return ky;
    }

    public void setKy(int ky) {
        this.ky = ky;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu == null ? BigDecimal.ZERO : doanhThu;
    }

    public BigDecimal getTienVon() {
        return tienVon;
    }

    public void setTienVon(BigDecimal tienVon) {
        this.tienVon = tienVon == null ? BigDecimal.ZERO : tienVon;
    }

    public BigDecimal getLoiNhuan() {
        return doanhThu.subtract(tienVon);
    }
}