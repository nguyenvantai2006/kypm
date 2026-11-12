package com.qlgiay.bus;

import com.qlgiay.dto.AuthSession;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoAuthAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthBUSDemoModeTest {

    @Test
    void demoLoginShouldWorkWithoutDatabase() {
        if (!DBConnect.isDemoMode()) {
            return;
        }

        AuthSession session = new AuthBUS().login(DemoAuthAccount.TAI_KHOAN, DemoAuthAccount.MAT_KHAU);
        assertNotNull(session);
        assertNotNull(session.getNhanVien());
        assertNotNull(session.getQuyen());
    }

    @Test
    void demoLoginShouldRejectWrongPassword() {
        if (!DBConnect.isDemoMode()) {
            return;
        }

        assertNull(new AuthBUS().login(DemoAuthAccount.TAI_KHOAN, "wrong-password"));
    }
}
