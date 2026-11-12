package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng DoiTraDTO")
class DoiTraDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính Đổi Trả")
    void testAllProperties() {
        DoiTraDTO dt = new DoiTraDTO();
        LocalDate now = LocalDate.now();
        BigDecimal tienHoan = new BigDecimal("2000000");

        dt.setMaDT("DT001");
        dt.setMaHD("HD001");
        dt.setNgayDoiTra(now);
        dt.setTongTienHoan(tienHoan);
        dt.setLyDo("Lỗi size");
        dt.setTinhTrang("Đã hoàn tiền"); // Kiểu String theo code gốc

        assertEquals("DT001", dt.getMaDT());
        assertEquals(now, dt.getNgayDoiTra());
        assertEquals(tienHoan, dt.getTongTienHoan());
        assertEquals("Đã hoàn tiền", dt.getTinhTrang());
        System.out.println(" -> [DoiTraDTOTest] Pass toàn bộ thuộc tính");
    }
}