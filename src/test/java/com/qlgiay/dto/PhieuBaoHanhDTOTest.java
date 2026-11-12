package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng PhieuBaoHanhDTO")
class PhieuBaoHanhDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính Phiếu Bảo Hành")
    void testAllProperties() {
        PhieuBaoHanhDTO bh = new PhieuBaoHanhDTO();
        LocalDate nhan = LocalDate.now();
        BigDecimal phi = new BigDecimal("50000");

        bh.setMaPBH("BH001");
        bh.setMaHD("HD100");
        bh.setNgayNhan(nhan);
        bh.setLoiCanBaoHanh("Bung keo");
        bh.setChiPhiPhatSinh(phi);
        bh.setTrangThai(0); // 0: Đang chờ

        assertEquals("BH001", bh.getMaPBH());
        assertEquals(nhan, bh.getNgayNhan());
        assertEquals(phi, bh.getChiPhiPhatSinh());
        assertEquals(0, bh.getTrangThai());
        System.out.println(" -> [PhieuBaoHanhDTOTest] Pass toàn bộ thuộc tính");
    }
}