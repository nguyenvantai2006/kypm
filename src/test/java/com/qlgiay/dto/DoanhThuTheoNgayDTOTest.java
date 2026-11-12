package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng DoanhThuTheoNgayDTO")
class DoanhThuTheoNgayDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính Doanh Thu Theo Ngày")
    void testProperties() {
        DoanhThuTheoNgayDTO dto = new DoanhThuTheoNgayDTO();
        LocalDate ngay = LocalDate.now();
        BigDecimal doanhThu = new BigDecimal("15000000");

        // Act
        dto.setNgay(ngay);
        dto.setDoanhThu(doanhThu);

        // Assert
        assertEquals(ngay, dto.getNgay(), "Ngày lấy ra phải khớp với ngày đã set");
        assertEquals(doanhThu, dto.getDoanhThu(), "Doanh thu lấy ra phải khớp với doanh thu đã set");

        System.out.println(" -> [DoanhThuTheoNgayDTOTest] Pass toàn bộ thuộc tính");
    }
}