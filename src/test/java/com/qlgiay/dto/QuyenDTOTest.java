package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng QuyenDTO")
class QuyenDTOTest {

    @Test
    @DisplayName("Kiểm tra các biến phân quyền (kiểu int)")
    void testAllProperties() {
        QuyenDTO q = new QuyenDTO();
        q.setMaQuyen("Q02");
        q.setTenQuyen("Quản lý");
        q.setQlBanHang(1);
        q.setQlNhanVien(0); // Quản lý không có quyền nhân viên

        assertEquals("Q02", q.getMaQuyen());
        assertEquals("Quản lý", q.getTenQuyen());
        assertEquals(1, q.getQlBanHang());
        assertEquals(0, q.getQlNhanVien());
        System.out.println(" -> [QuyenDTOTest] Pass toàn bộ thuộc tính");
    }
}