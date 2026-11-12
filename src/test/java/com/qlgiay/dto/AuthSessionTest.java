package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng AuthSession")
class AuthSessionTest {

    @Test
    @DisplayName("Kiểm tra lưu trữ phiên đăng nhập")
    void testAuthSessionProperties() {
        AuthSession session = new AuthSession();

        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNV("NV001");
        nv.setTaiKhoan("admin");

        QuyenDTO q = new QuyenDTO();
        q.setMaQuyen("Q01");
        q.setTenQuyen("Quản trị viên");

        session.setNhanVien(nv);
        session.setQuyen(q);

        assertNotNull(session.getNhanVien());
        assertEquals("NV001", session.getNhanVien().getMaNV());
        assertNotNull(session.getQuyen());
        assertEquals("Quản trị viên", session.getQuyen().getTenQuyen());
    }
}