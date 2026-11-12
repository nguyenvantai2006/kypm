package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng LoiNhuanTheoKyDTO (Có Output Console)")
class LoiNhuanTheoKyDTOTest {

    private LoiNhuanTheoKyDTO loiNhuanDTO;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[LoiNhuanTheoKyDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    // ==========================================
    // QUẢN LÝ VÒNG ĐỜI BÀI TEST (LIFECYCLE)
    // ==========================================
    @BeforeEach
    void setUp() {
        loiNhuanDTO = new LoiNhuanTheoKyDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST KHỞI TẠO & GÁN GIÁ TRỊ THỜI GIAN
    // ==========================================
    @Nested
    @DisplayName("1. Kiểm tra Dữ liệu Thời gian (Kỳ báo cáo)")
    class ThoiGianTests {

        @Test
        @DisplayName("Gán và lấy Kỳ hợp lệ")
        void testSetGetKy_Valid() {
            loiNhuanDTO.setKy(3);
            assertEqualsResult("setKy", "ky=3", 3, loiNhuanDTO.getKy());
        }

        @Test
        @DisplayName("Gán và lấy Năm hợp lệ")
        void testSetGetNam_Valid() {
            loiNhuanDTO.setNam(2026);
            assertEqualsResult("setNam", "nam=2026", 2026, loiNhuanDTO.getNam());
        }
    }

    // ==========================================
    // NHÓM 2. TEST DỮ LIỆU TÀI CHÍNH (BIGDECIMAL)
    // ==========================================
    @Nested
    @DisplayName("2. Kiểm tra Dữ liệu Tài chính (Doanh thu, Tiền vốn, Lợi nhuận)")
    class TaiChinhTests {

        @Test
        @DisplayName("Xử lý đúng Doanh thu và Tiền vốn hợp lệ (Có lãi)")
        void testGiaTriTaiChinh_CoLai() {
            // Act
            BigDecimal doanhThu = new BigDecimal("50000000"); // 50 triệu
            BigDecimal tienVon = new BigDecimal("30000000"); // 30 triệu

            loiNhuanDTO.setDoanhThu(doanhThu);
            loiNhuanDTO.setTienVon(tienVon);

            // Assert
            assertEqualsResult("Doanh thu", "50M", doanhThu, loiNhuanDTO.getDoanhThu());
            assertEqualsResult("Tien von", "30M", tienVon, loiNhuanDTO.getTienVon());
            assertEqualsResult("Loi nhuan (Lai)", "50M - 30M", new BigDecimal("20000000"), loiNhuanDTO.getLoiNhuan());
        }

        @Test
        @DisplayName("Xử lý đúng trường hợp Lỗ (Lợi nhuận âm)")
        void testGiaTriTaiChinh_BiLo() {
            // Act
            BigDecimal doanhThu = new BigDecimal("15000000"); // 15 triệu
            BigDecimal tienVon = new BigDecimal("20000000"); // 20 triệu

            loiNhuanDTO.setDoanhThu(doanhThu);
            loiNhuanDTO.setTienVon(tienVon);

            // Assert
            assertEqualsResult("Loi nhuan (Lo)", "15M - 20M", new BigDecimal("-5000000"), loiNhuanDTO.getLoiNhuan());
        }

        @Test
        @DisplayName("Xử lý an toàn giá trị Null (Tự động chuyển về ZERO)")
        void testGiaTriTaiChinh_NullSafe() {
            // Act
            loiNhuanDTO.setDoanhThu(null);
            loiNhuanDTO.setTienVon(null);

            // Assert
            assertEqualsResult("Doanh thu null an toan", "null", BigDecimal.ZERO, loiNhuanDTO.getDoanhThu());
            assertEqualsResult("Tien von null an toan", "null", BigDecimal.ZERO, loiNhuanDTO.getTienVon());
            assertEqualsResult("Loi nhuan khi null", "0 - 0", BigDecimal.ZERO, loiNhuanDTO.getLoiNhuan());
        }
    }
}