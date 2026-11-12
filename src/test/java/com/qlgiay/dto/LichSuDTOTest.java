package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng LichSuDTO")
class LichSuDTOTest {

    @Test
    @DisplayName("Kiểm tra toàn bộ thuộc tính Lịch Sử Hoạt Động")
    void testAllProperties() {
        LichSuDTO log = new LichSuDTO();
        LocalDateTime time = LocalDateTime.now();

        log.setMaLog(1);
        log.setMaNV("NV001");
        log.setPhanLoai("GIAO_DICH");
        log.setHanhDong("Bán hàng");
        log.setDoiTuong("Hóa đơn HD001");
        log.setThoiGian(time);
        log.setChiTiet("Thanh toán thành công");

        assertEquals(1, log.getMaLog());
        assertEquals("NV001", log.getMaNV());
        assertEquals("GIAO_DICH", log.getPhanLoai());
        assertEquals("Bán hàng", log.getHanhDong());
        assertEquals("Hóa đơn HD001", log.getDoiTuong());
        assertEquals(time, log.getThoiGian());
        assertEquals("Thanh toán thành công", log.getChiTiet());
    }
}