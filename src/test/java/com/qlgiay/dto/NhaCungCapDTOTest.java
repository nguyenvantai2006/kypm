package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng NhaCungCapDTO")
class NhaCungCapDTOTest {

    @Test
    @DisplayName("Kiểm tra toàn bộ thuộc tính Nhà Cung Cấp")
    void testAllProperties() {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNCC("NCC001");
        ncc.setTenNCC("Nike VN");
        ncc.setSdt("0987654321");
        ncc.setDiaChi("TP.HCM");
        ncc.setTrangThai(1);

        assertEquals("NCC001", ncc.getMaNCC());
        assertEquals("Nike VN", ncc.getTenNCC());
        assertEquals("0987654321", ncc.getSdt());
        assertEquals(1, ncc.getTrangThai());
        System.out.println(" -> [NhaCungCapDTOTest] Pass toàn bộ thuộc tính");
    }
}