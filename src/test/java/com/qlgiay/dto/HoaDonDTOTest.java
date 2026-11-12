package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng HoaDonDTO (Có Output Console)")
class HoaDonDTOTest {

    private HoaDonDTO hoaDon;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[HoaDonDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        hoaDon = new HoaDonDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Nested
    @DisplayName("1. Kiểm tra Dữ liệu Khóa ngoại và Trạng thái")
    class ThongTinChungTests {

        @Test
        @DisplayName("Gán và lấy Mã Hóa Đơn")
        void testSetGetMaHD() {
            hoaDon.setMaHD("HD1001");
            assertEqualsResult("setMaHD", "HD1001", "HD1001", hoaDon.getMaHD());
        }

        @Test
        @DisplayName("Gán và lấy Mã Khách Hàng")
        void testSetGetMaKH() {
            hoaDon.setMaKH("KH005");
            assertEqualsResult("setMaKH", "KH005", "KH005", hoaDon.getMaKH());
        }
    }

    @Nested
    @DisplayName("2. Kiểm tra Thời gian và Tổng tiền")
    class ThoiGianVaTienTeTests {

        @Test
        @DisplayName("Gán và lấy Ngày Lập Hóa Đơn (LocalDate)")
        void testSetGetNgayLap() {
            LocalDate now = LocalDate.now();
            hoaDon.setNgayLap(now);
            assertEqualsResult("setNgayLap", now.toString(), now, hoaDon.getNgayLap());
        }

        @Test
        @DisplayName("Gán và lấy Tổng Tiền Hóa Đơn")
        void testSetGetTongTien() {
            BigDecimal tongTien = new BigDecimal("5000000");
            hoaDon.setTongTien(tongTien);
            assertEqualsResult("setTongTien", "5M", tongTien, hoaDon.getTongTien());
        }
    }
}