package com.qlgiay.bus;

import com.qlgiay.dto.DoiTraDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp DoiTraBUS (Có Output Console)")
class DoiTraBUSTest {

    private DoiTraBUS doiTraBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[DoiTraBUSTest] %s | input=%s | expected=false | actual=%b%n",
                testCase, input, actual);
        assertFalse(actual);
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
        doiTraBUS = new DoiTraBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM createReturn() (Phiếu Đổi Trả)
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Lập Phiếu Đổi Trả")
    class CreateReturnTests {

        @Test
        @DisplayName("Lập phiếu thất bại do đối tượng truyền vào bị null")
        void testCreateReturn_NullObject_ReturnsFalse() {
            boolean result = doiTraBUS.createReturn(null);
            assertFalseResult("Tao phieu doi tra null", "dt=null", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do bỏ trống Mã Đổi Trả")
        void testCreateReturn_EmptyMaDT_ReturnsFalse() {
            // Đã sửa thứ tự tham số: BigDecimal tongTienHoan nằm trước lyDo và tinhTrang
            DoiTraDTO dt = new DoiTraDTO("", "HD01", "NV01", "SP01",
                    LocalDate.now(), 1, BigDecimal.ZERO, "Hàng lỗi", "Nguyên vẹn");

            boolean result = doiTraBUS.createReturn(dt);
            assertFalseResult("Tao phieu thieu MaDT", "MaDT=''", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do Số lượng đổi trả <= 0")
        void testCreateReturn_InvalidQuantity_ReturnsFalse() {
            DoiTraDTO dt = new DoiTraDTO("DT01", "HD01", "NV01", "SP01",
                    LocalDate.now(), 0, BigDecimal.ZERO, "Hàng lỗi", "Nguyên vẹn");

            boolean result = doiTraBUS.createReturn(dt);
            assertFalseResult("Tao phieu so luong khong hop le", "SoLuong=0", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do bỏ trống Lý do")
        void testCreateReturn_EmptyReason_ReturnsFalse() {
            DoiTraDTO dt = new DoiTraDTO("DT01", "HD01", "NV01", "SP01",
                    LocalDate.now(), 1, BigDecimal.ZERO, "", "Nguyên vẹn");

            boolean result = doiTraBUS.createReturn(dt);
            assertFalseResult("Tao phieu thieu Ly Do", "LyDo=''", result);
        }
    }
}