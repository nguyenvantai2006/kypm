package com.qlgiay.util;

import java.math.BigDecimal;

import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.dto.QuyenDTO;

public final class DemoAuthAccount {
    public static final String TAI_KHOAN = "demo";
    public static final String MAT_KHAU = "demo123";

    private DemoAuthAccount() {
    }

    public static NhanVienDTO createNhanVien() {
        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNV("DEMO001");
        nv.setHo("Demo");
        nv.setTen("Admin");
        nv.setMaQuyen("Q01");
        nv.setTaiKhoan(TAI_KHOAN);
        nv.setMatKhau(MAT_KHAU);
        nv.setLuong(BigDecimal.valueOf(20000000));
        nv.setTrangThai(1);
        return nv;
    }

    public static QuyenDTO createQuyen() {
        return new QuyenDTO("Q01", "Admin Demo", 1, 1, 1, 1, 1, 1);
    }
}
