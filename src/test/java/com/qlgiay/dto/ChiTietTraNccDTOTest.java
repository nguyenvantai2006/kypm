package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng ChiTietTraNccDTO")
class ChiTietTraNccDTOTest {

    private ChiTietTraNccDTO chiTiet;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[ChiTietTraNccDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        chiTiet = new ChiTietTraNccDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Gán và lấy thông tin Chi tiết trả NCC")
    void testSetGetInfo() {
        BigDecimal giaNhap = new BigDecimal("500000");

        chiTiet.setMaPT("PT001");
        chiTiet.setMaSP("SP005");
        chiTiet.setSoLuong(10);
        chiTiet.setGiaNhap(giaNhap);

        assertEqualsResult("setMaPT", "PT001", "PT001", chiTiet.getMaPT());
        assertEqualsResult("setMaSP", "SP005", "SP005", chiTiet.getMaSP());
        assertEqualsResult("setSoLuong", "10", 10, chiTiet.getSoLuong());
        assertEqualsResult("setGiaNhap", "500K", giaNhap, chiTiet.getGiaNhap());
    }
}