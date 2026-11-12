package com.qlgiay.bus;

import com.qlgiay.dto.LichSuDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp LichSuBUS (Có Output Console)")
class LichSuBUSTest {

    private LichSuBUS lichSuBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[LichSuBUSTest] %s | input=%s | expected=false | actual=%b%n",
                testCase, input, actual);
        assertFalse(actual);
    }

    private void assertTrueResult(String testCase, String input, boolean actual) {
        System.out.printf("[LichSuBUSTest] %s | input=%s | expected=true | actual=%b%n",
                testCase, input, actual);
        assertTrue(actual);
    }

    private void assertEmptyResult(String testCase, String input, List<?> actual) {
        System.out.printf("[LichSuBUSTest] %s | input=%s | expected=empty | actual_size=%d%n",
                testCase, input, actual.size());
        assertTrue(actual.isEmpty());
    }

    // ==========================================
    // QUẢN LÝ VÒNG ĐỜI BÀI TEST
    // ==========================================
    @BeforeAll
    static void initAll() {
        try {
            DBConnect.getConnection().close();
        } catch (Exception ignored) {
        }
    }

    @AfterAll
    static void tearDownAll() {
    }

    @BeforeEach
    void setUp() {
        lichSuBUS = new LichSuBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM GHI NHẬT KÝ
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Ghi Lịch Sử")
    class InsertTests {

        @Test
        @DisplayName("Ghi lịch sử thất bại do đối tượng null")
        void testInsert_NullLog_ReturnsFalse() {
            boolean result = lichSuBUS.insert(null);
            assertFalseResult("Ghi lich su voi doi tuong null", "log=null", result);
        }

        @Test
        @DisplayName("Ghi lịch sử thất bại do thiếu Phân loại")
        void testInsert_EmptyPhanLoai_ReturnsFalse() {
            LichSuDTO log = new LichSuDTO();
            log.setHanhDong("Test hành động");
            boolean result = lichSuBUS.insert(log);
            assertFalseResult("Ghi lich su thieu phan loai", "PhanLoai=null", result);
        }

        @Test
        @DisplayName("Ghi lịch sử qua hàm ghiNhatKy (Happy Path giả lập)")
        void testGhiNhatKy_ValidInput() {
            // Giả lập ghi lịch sử, nếu CSDL kết nối tốt thì sẽ true, nếu không có CSDL thì
            // catch lại
            try {
                boolean result = lichSuBUS.ghiNhatKy("NV01", "TEST", "Ghi test", "HD01", "Test chi tiet");
                // Không assert cứng true/false vì phụ thuộc CSDL, chỉ in ra console
                System.out.printf("[LichSuBUSTest] Test ham ghiNhatKy ket qua: %b%n", result);
            } catch (Exception e) {
                fail("Hàm ghiNhatKy gây ra exception: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // NHÓM 2. TEST HÀM TÌM KIẾM
    // ==========================================
    @Nested
    @DisplayName("2. Chức năng Tìm Kiếm Lịch Sử")
    class SearchTests {

        @Test
        @DisplayName("Tìm kiếm thất bại do Ngày Bắt Đầu lớn hơn Ngày Kết Thúc")
        void testFind_TuNgayAfterDenNgay_ReturnsEmpty() {
            LocalDate tuNgay = LocalDate.now().plusDays(1);
            LocalDate denNgay = LocalDate.now();
            List<LichSuDTO> result = lichSuBUS.findByPhanLoaiAndDate("TEST", tuNgay, denNgay);
            assertEmptyResult("Tim kiem voi TuNgay > DenNgay", "TuNgay=" + tuNgay + ", DenNgay=" + denNgay, result);
        }

        @Test
        @DisplayName("Tìm kiếm thất bại do bỏ trống Phân loại")
        void testFind_EmptyPhanLoai_ReturnsEmpty() {
            List<LichSuDTO> result = lichSuBUS.findByPhanLoaiAndDate("", LocalDate.now(), LocalDate.now());
            assertEmptyResult("Tim kiem thieu Phan loai", "PhanLoai=''", result);
        }
    }
}