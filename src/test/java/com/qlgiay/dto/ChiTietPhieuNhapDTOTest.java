package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng ChiTietPhieuNhapDTO")
class ChiTietPhieuNhapDTOTest {

    private ChiTietPhieuNhapDTO chiTiet;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[ChiTietPhieuNhapDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        chiTiet = new ChiTietPhieuNhapDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Nested
    @DisplayName("Kiểm tra Khóa ngoại và Giá nhập")
    class ThongTinChiTietTests {

        @Test
        @DisplayName("Gán và lấy thông tin Phiếu nhập")
        void testSetGetKeysAndValues() {
            BigDecimal giaNhap = new BigDecimal("800000");

            chiTiet.setMaPN("PN1001");
            chiTiet.setMaSP("SP002");
            chiTiet.setSoLuong(50);
            chiTiet.setGiaNhap(giaNhap);

            assertEqualsResult("setMaPN", "PN1001", "PN1001", chiTiet.getMaPN());
            assertEqualsResult("setMaSP", "SP002", "SP002", chiTiet.getMaSP());
            assertEqualsResult("setSoLuong", "50", 50, chiTiet.getSoLuong());
            assertEqualsResult("setGiaNhap", "800K", giaNhap, chiTiet.getGiaNhap());
        }
    }
}