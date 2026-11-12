package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng PhieuNhapDTO")
class PhieuNhapDTOTest {

    private PhieuNhapDTO phieuNhap;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[PhieuNhapDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        phieuNhap = new PhieuNhapDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Kiểm tra toàn bộ thuộc tính Phiếu Nhập")
    void testAllProperties() {
        LocalDate now = LocalDate.now();
        BigDecimal tongTien = new BigDecimal("15000000");

        phieuNhap.setMaPN("PN001");
        phieuNhap.setMaNV("NV001");
        phieuNhap.setMaNCC("NCC001");
        phieuNhap.setNgayNhap(now);
        phieuNhap.setTongSoMatHang(100);
        phieuNhap.setTongTien(tongTien);

        assertEqualsResult("MaPN", "PN001", "PN001", phieuNhap.getMaPN());
        assertEqualsResult("MaNV", "NV001", "NV001", phieuNhap.getMaNV());
        assertEqualsResult("MaNCC", "NCC001", "NCC001", phieuNhap.getMaNCC());
        assertEqualsResult("NgayNhap", now.toString(), now, phieuNhap.getNgayNhap());
        assertEqualsResult("TongSoMatHang", "100", 100, phieuNhap.getTongSoMatHang());
        assertEqualsResult("TongTien", "15M", tongTien, phieuNhap.getTongTien());
    }
}