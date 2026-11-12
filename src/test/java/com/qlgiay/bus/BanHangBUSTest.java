package com.qlgiay.bus;

import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp BanHangBUS (Có Output Console)")
class BanHangBUSTest {

    private BanHangBUS banHangBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[BanHangBUSTest] %s | input=%s | expected=false | actual=%b%n",
                testCase, input, actual);
        assertFalse(actual);
    }

    private void assertTrueResult(String testCase, String input, boolean actual) {
        System.out.printf("[BanHangBUSTest] %s | input=%s | expected=true | actual=%b%n",
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
        banHangBUS = new BanHangBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM createInvoice() (Tạo Hóa Đơn)
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Lập Hóa Đơn Bán Hàng")
    class CreateInvoiceTests {

        @Test
        @DisplayName("Lập hóa đơn thất bại do Hóa đơn null")
        void testCreate_NullInvoice_ReturnsFalse() {
            List<ChiTietHoaDonDTO> items = new ArrayList<>();
            items.add(new ChiTietHoaDonDTO("HD01", "SP01", 1, BigDecimal.ZERO));

            boolean result = banHangBUS.createInvoice(null, items, 0);
            assertFalseResult("Tao hoa don voi hd=null", "HoaDon=null", result);
        }

        @Test
        @DisplayName("Lập hóa đơn thất bại do Danh sách chi tiết (Items) rỗng")
        void testCreate_EmptyItems_ReturnsFalse() {
            HoaDonDTO hd = new HoaDonDTO();
            hd.setMaHD("HD01");
            hd.setMaNV("NV01");

            boolean result = banHangBUS.createInvoice(hd, new ArrayList<>(), 0);
            assertFalseResult("Tao hoa don voi items rong", "items.size=0", result);
        }

        @Test
        @DisplayName("Lập hóa đơn thất bại do bỏ trống Mã Nhân Viên")
        void testCreate_EmptyMaNV_ReturnsFalse() {
            HoaDonDTO hd = new HoaDonDTO();
            hd.setMaHD("HD01");
            hd.setMaNV(""); // Lỗi ở đây

            List<ChiTietHoaDonDTO> items = new ArrayList<>();
            items.add(new ChiTietHoaDonDTO("HD01", "SP01", 1, BigDecimal.ZERO));

            boolean result = banHangBUS.createInvoice(hd, items, 0);
            assertFalseResult("Tao hoa don voi MaNV rong", "MaNV=''", result);
        }

        @Test
        @DisplayName("Lập hóa đơn thất bại do Số lượng sản phẩm <= 0")
        void testCreate_InvalidItemQuantity_ReturnsFalse() {
            HoaDonDTO hd = new HoaDonDTO();
            hd.setMaHD("HD01");
            hd.setMaNV("NV01");

            List<ChiTietHoaDonDTO> items = new ArrayList<>();
            // Cố tình truyền số lượng = 0
            items.add(new ChiTietHoaDonDTO("HD01", "SP01", 0, BigDecimal.ZERO));

            boolean result = banHangBUS.createInvoice(hd, items, 0);
            assertFalseResult("Tao hoa don voi SP co SoLuong <= 0", "SoLuong=0", result);
        }
    }
}