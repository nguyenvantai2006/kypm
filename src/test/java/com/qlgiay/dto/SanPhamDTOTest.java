package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng SanPhamDTO (Có Output Console)")
class SanPhamDTOTest {

    private SanPhamDTO sanPham;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[SanPhamDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        sanPham = new SanPhamDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Nested
    @DisplayName("1. Kiểm tra thông tin cơ bản")
    class ThongTinCoBanTests {

        @Test
        @DisplayName("Gán và lấy Mã sản phẩm")
        void testSetGetMaSP() {
            sanPham.setMaSP("SP001");
            assertEqualsResult("setMaSP", "SP001", "SP001", sanPham.getMaSP());
        }

        @Test
        @DisplayName("Gán và lấy Tên sản phẩm")
        void testSetGetTenSP() {
            sanPham.setTenSP("Giày Nike Air Force 1");
            assertEqualsResult("setTenSP", "Giày Nike...", "Giày Nike Air Force 1", sanPham.getTenSP());
        }

        @Test
        @DisplayName("Gán và lấy Số lượng tồn kho")
        void testSetGetSoLuong() {
            sanPham.setSoLuong(50);
            assertEqualsResult("setSoLuong", "50", 50, sanPham.getSoLuong());
        }
    }

    @Nested
    @DisplayName("2. Kiểm tra Dữ liệu Tiền tệ (Đơn giá)")
    class TaiChinhTests {

        @Test
        @DisplayName("Gán và lấy Đơn giá hợp lệ")
        void testSetGetDonGia_Valid() {
            BigDecimal donGia = new BigDecimal("2500000");
            sanPham.setDonGia(donGia);
            assertEqualsResult("setDonGia", "2.5M", donGia, sanPham.getDonGia());
        }

        @Test
        @DisplayName("Gán giá trị Null cho Đơn giá (Kiểm tra NullPointerException)")
        void testSetGetDonGia_Null() {
            sanPham.setDonGia(null);
            assertNull(sanPham.getDonGia(), "Đơn giá nên cho phép null khi chưa có dữ liệu");
        }
    }
}