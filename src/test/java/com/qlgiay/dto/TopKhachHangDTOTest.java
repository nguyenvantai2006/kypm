package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng TopKhachHangDTO")
class TopKhachHangDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính Top Khách Hàng")
    void testProperties() {
        TopKhachHangDTO dto = new TopKhachHangDTO();
        BigDecimal chiTieu = new BigDecimal("25000000");

        dto.setMaKH("KH001");
        dto.setTenKH("Khách hàng VIP");
        dto.setSoHoaDon(10);
        dto.setTongChiTieu(chiTieu);

        assertEquals("KH001", dto.getMaKH());
        assertEquals("Khách hàng VIP", dto.getTenKH());
        assertEquals(10, dto.getSoHoaDon());
        assertEquals(chiTieu, dto.getTongChiTieu());
    }
}