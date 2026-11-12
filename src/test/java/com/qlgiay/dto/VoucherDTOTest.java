package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng VoucherDTO")
class VoucherDTOTest {

    @Test
    @DisplayName("Kiểm tra thuộc tính thời gian và tiền tệ Voucher")
    void testAllProperties() {
        VoucherDTO v = new VoucherDTO();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(10);
        BigDecimal maxDiscount = new BigDecimal("100000");

        v.setMaVoucher("SALE10");
        v.setTenVoucher("Giảm 10%");
        v.setPhanTramGiam(10);
        v.setGiamToiDa(maxDiscount);
        v.setNgayBatDau(start);
        v.setNgayKetThuc(end);
        v.setTrangThai(1);

        assertEquals("SALE10", v.getMaVoucher());
        assertEquals(10, v.getPhanTramGiam());
        assertEquals(maxDiscount, v.getGiamToiDa());
        assertEquals(start, v.getNgayBatDau());
        assertEquals(end, v.getNgayKetThuc());
        System.out.println(" -> [VoucherDTOTest] Pass toàn bộ thuộc tính");
    }
}