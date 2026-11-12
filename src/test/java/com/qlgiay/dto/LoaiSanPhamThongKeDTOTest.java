package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng LoaiSanPhamThongKeDTO")
class LoaiSanPhamThongKeDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính Thống kê theo loại sản phẩm")
    void testProperties() {
        LoaiSanPhamThongKeDTO dto = new LoaiSanPhamThongKeDTO();
        dto.setLoaiSP("Giày chạy bộ");
        dto.setSoLuongBan(150);

        assertEquals("Giày chạy bộ", dto.getLoaiSP());
        assertEquals(150, dto.getSoLuongBan());
    }
}