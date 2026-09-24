package com.qlgiay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DoanhThuTheoNgayDTO {
    private LocalDate ngay;
    private BigDecimal doanhThu;

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu;
    }
}