package com.qlgiay.bus;

import com.qlgiay.dto.PhieuBaoHanhDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp BaoHanhBUS (Có Output Console)")
class BaoHanhBUSTest {

    private BaoHanhBUS baoHanhBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[BaoHanhBUSTest] %s | input=%s | expected=false | actual=%b%n",
                testCase, input, actual);
        assertFalse(actual);
    }

    private void assertTrueResult(String testCase, String input, boolean actual) {
        System.out.printf("[BaoHanhBUSTest] %s | input=%s | expected=true | actual=%b%n",
                testCase, input, actual);
        assertTrue(actual);
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
        baoHanhBUS = new BaoHanhBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM isValidCommon() & create()
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Lập Phiếu Bảo Hành")
    class CreateBaoHanhTests {

        @Test
        @DisplayName("Thêm thất bại do dữ liệu Null")
        void testCreate_NullObject_ReturnsFalse() {
            boolean result = baoHanhBUS.create(null);
            assertFalseResult("Tao phieu bao hanh null", "pbh=null", result);
        }

        @Test
        @DisplayName("Thêm thất bại do thiếu thông tin bắt buộc (Mã PBH rỗng)")
        void testCreate_EmptyMaPBH_ReturnsFalse() {
            PhieuBaoHanhDTO pbh = new PhieuBaoHanhDTO("", "HD01", "SP01", "KH01",
                    LocalDate.now(), LocalDate.now().plusDays(5), "Bung keo", BigDecimal.ZERO, 0);

            boolean result = baoHanhBUS.create(pbh);
            assertFalseResult("Tao phieu thieu MaPBH", "MaPBH=''", result);
        }

        @Test
        @DisplayName("Thêm thất bại do Ngày trả dự kiến nằm trong quá khứ")
        void testCreate_InvalidReturnDate_ReturnsFalse() {
            PhieuBaoHanhDTO pbh = new PhieuBaoHanhDTO("PBH01", "HD01", "SP01", "KH01",
                    LocalDate.now(), LocalDate.now().minusDays(1), "Bung keo", BigDecimal.ZERO, 0);

            boolean result = baoHanhBUS.create(pbh);
            assertFalseResult("Tao phieu co NgayTra trong qua khu (TrangThai != 1)", "NgayTraDuKien < Now", result);
        }

        @Test
        @DisplayName("Thêm thất bại do Chi phí phát sinh bị âm")
        void testCreate_NegativeCost_ReturnsFalse() {
            PhieuBaoHanhDTO pbh = new PhieuBaoHanhDTO("PBH01", "HD01", "SP01", "KH01",
                    LocalDate.now(), LocalDate.now().plusDays(5), "Bung keo", new BigDecimal("-50000"), 0);

            boolean result = baoHanhBUS.create(pbh);
            assertFalseResult("Tao phieu co ChiPhi < 0", "ChiPhiPhatSinh=-50000", result);
        }
    }
}