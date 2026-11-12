package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng TopSanPhamDTO")
class TopSanPhamDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính Top Sản Phẩm")
    void testProperties() {
        TopSanPhamDTO dto = new TopSanPhamDTO();
        BigDecimal doanhThu = new BigDecimal("15000000");

        dto.setMaSP("SP001");
        dto.setTenSP("Sản phẩm Hot");
        dto.setSoLuongBan(50);
        dto.setDoanhThu(doanhThu);

        assertEquals("SP001", dto.getMaSP());
        assertEquals("Sản phẩm Hot", dto.getTenSP());
        assertEquals(50, dto.getSoLuongBan());
        assertEquals(doanhThu, dto.getDoanhThu());
    }
}