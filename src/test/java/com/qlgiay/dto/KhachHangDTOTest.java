package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng KhachHangDTO")
class KhachHangDTOTest {

    private KhachHangDTO kh;

    @BeforeEach
    void setUp() {
        kh = new KhachHangDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Kiểm tra toàn bộ thuộc tính Khách Hàng")
    void testAllProperties() {
        kh.setMaKH("KH001");
        kh.setTenKH("Nguyễn Văn A");
        kh.setSdt("0901234567");
        kh.setDiaChi("123 Lê Lợi");
        kh.setDiemTichLuy(150);
        kh.setTrangThai(1);

        assertEquals("KH001", kh.getMaKH());
        assertEquals("Nguyễn Văn A", kh.getTenKH());
        assertEquals("0901234567", kh.getSdt());
        assertEquals("123 Lê Lợi", kh.getDiaChi());
        assertEquals(150, kh.getDiemTichLuy());
        assertEquals(1, kh.getTrangThai());
        System.out.println("[KhachHangDTOTest] Pass toàn bộ thuộc tính");
    }
}