package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng ChiTietHoaDonDTO")
class ChiTietHoaDonDTOTest {

    private ChiTietHoaDonDTO chiTiet;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[ChiTietHoaDonDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        chiTiet = new ChiTietHoaDonDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Nested
    @DisplayName("Kiểm tra Khóa ngoại và Dữ liệu Tài chính")
    class ThongTinChiTietTests {

        @Test
        @DisplayName("Gán và lấy Mã Hóa Đơn, Mã Sản Phẩm")
        void testSetGetKeys() {
            chiTiet.setMaHD("HD1001");
            chiTiet.setMaSP("SP001");
            assertEqualsResult("setMaHD", "HD1001", "HD1001", chiTiet.getMaHD());
            assertEqualsResult("setMaSP", "SP001", "SP001", chiTiet.getMaSP());
        }

        @Test
        @DisplayName("Gán và lấy Số lượng, Đơn giá")
        void testSetGetSoLuongDonGia() {
            BigDecimal donGia = new BigDecimal("1500000");
            chiTiet.setSoLuong(2);
            chiTiet.setDonGia(donGia);

            assertEqualsResult("setSoLuong", "2", 2, chiTiet.getSoLuong());
            assertEqualsResult("setDonGia", "1.5M", donGia, chiTiet.getDonGia());
        }
    }
}