package com.qlgiay.bus;

import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp ThongKeBUS (Có Output Console)")
class ThongKeBUSTest {

    private ThongKeBUS thongKeBUS;
    private LocalDate today;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertNoException(String testCase, String input, Runnable action) {
        System.out.printf("[ThongKeBUSTest] %s | input=%s | expected=NoException | actual=",
                testCase, input);
        try {
            action.run();
            System.out.println("Success");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            fail("Khong nen nem ra exception");
        }
    }

    // ==========================================
    // QUẢN LÝ VÒNG ĐỜI BÀI TEST
    // ==========================================
    @BeforeAll
    static void initAll() {
        try {
            DBConnect.enableTestConnection();
        } catch (Exception ignored) {
        }
    }

    @AfterAll
    static void tearDownAll() {
        DBConnect.closeTestConnection();
    }

    @BeforeEach
    void setUp() {
        thongKeBUS = new ThongKeBUS();
        today = LocalDate.now();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM GỌI DAO
    // ==========================================
    @Nested
    @DisplayName("1. Truy xuất dữ liệu Thống Kê")
    class RetrieveDataTests {

        @Test
        @DisplayName("Kiểm tra hàm getTongDoanhThu không văng exception")
        void testGetTongDoanhThu_DoesNotThrow() {
            assertNoException("Lay tong doanh thu hien tai", "tuNgay=today, denNgay=today", () -> {
                thongKeBUS.getTongDoanhThu(today, today);
            });
        }

        @Test
        @DisplayName("Kiểm tra hàm getSoHoaDon không văng exception")
        void testGetSoHoaDon_DoesNotThrow() {
            assertNoException("Lay so hoa don", "tuNgay=today, denNgay=today", () -> {
                thongKeBUS.getSoHoaDon(today, today);
            });
        }

        @Test
        @DisplayName("Kiểm tra hàm getTopSanPham không văng exception")
        void testGetTopSanPham_DoesNotThrow() {
            assertNoException("Lay top san pham", "tuNgay=today, denNgay=today", () -> {
                thongKeBUS.getTopSanPham(today, today);
            });
        }
    }
}