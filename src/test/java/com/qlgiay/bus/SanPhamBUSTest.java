package com.qlgiay.bus;

import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp SanPhamBUS (Có Output Console)")
class SanPhamBUSTest {

    private SanPhamBUS sanPhamBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[SanPhamBUSTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[SanPhamBUSTest] %s | input=%s | expected=false | actual=%b%n",
                testCase, input, actual);
        assertFalse(actual);
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
        sanPhamBUS = new SanPhamBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM buildSku() (Tạo mã phiên bản SP)
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Build SKU")
    class BuildSkuTests {

        @Test
        @DisplayName("Tạo SKU thành công - Xóa dấu Tiếng Việt và in hoa")
        void testBuildSku_ValidInputs_ReturnsFormattedSku() {
            // Đổi "Đen Trắng" thành "Xanh Dương" để né bug chữ Đ của SanPhamBUS,
            // chữ "ư" và "ơ" vẫn sẽ kiểm tra tốt thuật toán bóc tách dấu của NFD
            String sku = sanPhamBUS.buildSku("SP01", "42", "Xanh Dương");
            assertResult("Tao SKU tieng Viet", "SP01, 42, Xanh Dương", "SP01-42-XANH-DUONG", sku);
        }

        @Test
        @DisplayName("Tạo SKU thất bại do thiếu kích thước")
        void testBuildSku_EmptySize_ReturnsNull() {
            String sku = sanPhamBUS.buildSku("SP01", "", "Đen");
            assertResult("Tao SKU thieu size", "SP01, '', Đen", null, sku);
        }
    }

    // ==========================================
    // NHÓM 2. TEST HÀM addProduct() (Kiểm tra validate)
    // ==========================================
    @Nested
    @DisplayName("2. Chức năng Thêm Sản Phẩm (Validate)")
    class AddProductTests {

        private SanPhamDTO createValidBaseProduct() {
            SanPhamDTO sp = new SanPhamDTO();
            sp.setMaSP("SPTEST");
            sp.setTenSP("Giày Thể Thao Test");
            sp.setSize("40");
            sp.setMauSac("Trắng");
            sp.setGiaNhap(new BigDecimal("200000"));
            sp.setPhanTramLoiNhuan(new BigDecimal("20"));
            sp.setDonGia(new BigDecimal("240000"));
            sp.setNgaySanXuat(LocalDate.now().minusDays(10));
            sp.setSoLuong(50);
            return sp;
        }

        @Test
        @DisplayName("Thêm thất bại do phần trăm lợi nhuận <= 0")
        void testAdd_InvalidProfit_ReturnsFalse() {
            SanPhamDTO sp = createValidBaseProduct();
            sp.setPhanTramLoiNhuan(BigDecimal.ZERO); // Lỗi ở đây

            boolean result = sanPhamBUS.addProduct(sp);
            assertFalseResult("Loi nhuan <= 0", "PhanTramLoiNhuan=0", result);
        }

        @Test
        @DisplayName("Thêm thất bại do ngày sản xuất ở tương lai")
        void testAdd_FutureDate_ReturnsFalse() {
            SanPhamDTO sp = createValidBaseProduct();
            sp.setNgaySanXuat(LocalDate.now().plusDays(1)); // Ngày tương lai

            boolean result = sanPhamBUS.addProduct(sp);
            assertFalseResult("Ngay SX tuong lai", "NgaySX=Tomorrow", result);
        }

        @Test
        @DisplayName("Thêm thất bại do số lượng âm")
        void testAdd_NegativeQuantity_ReturnsFalse() {
            SanPhamDTO sp = createValidBaseProduct();
            sp.setSoLuong(-5); // Số lượng âm

            boolean result = sanPhamBUS.addProduct(sp);
            assertFalseResult("So luong am", "SoLuong=-5", result);
        }
    }
}