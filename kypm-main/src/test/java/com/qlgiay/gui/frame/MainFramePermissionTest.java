package com.qlgiay.gui.frame;

import com.qlgiay.dto.QuyenDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainFramePermissionTest {
    @Test
    void customerAndVoucherUseCustomerPermission() {
        assertEquals(MainFrame.permissionForKey("KHACHHANG"), MainFrame.Permission.QL_KHACHHANG);
        assertEquals(MainFrame.permissionForKey("VOUCHER"), MainFrame.Permission.QL_KHACHHANG);
    }

    @Test
    void salesStillUseSalesPermission() {
        assertEquals(MainFrame.permissionForKey("BANHANG"), MainFrame.Permission.QL_BANHANG);
        assertEquals(MainFrame.permissionForKey("HOADON"), MainFrame.Permission.QL_BANHANG);
    }

    @Test
    void permissionCheckMatchesRoleFlags() {
        QuyenDTO q = new QuyenDTO();
        q.setQlBanHang(1);
        q.setQlKhachHang(1);

        assertEquals(1, q.getQlBanHang());
        assertEquals(1, q.getQlKhachHang());
    }
}
