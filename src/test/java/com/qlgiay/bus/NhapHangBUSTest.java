package com.qlgiay.bus;

import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.dto.PhieuNhapDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp NhapHangBUS (Có Output Console)")
class NhapHangBUSTest {

    private NhapHangBUS nhapHangBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[NhapHangBUSTest] %s | input=%s | expected=false | actual=%b%n",
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
        nhapHangBUS = new NhapHangBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM createImport() (Nhập hàng & Cộng kho)
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Lập Phiếu Nhập")
    class CreateImportTests {

        @Test
        @DisplayName("Lập phiếu thất bại do Phiếu nhập bị null")
        void testCreate_NullPhieuNhap_ReturnsFalse() {
            List<ChiTietPhieuNhapDTO> items = new ArrayList<>();
            items.add(new ChiTietPhieuNhapDTO("PN01", "SP01", 10, new BigDecimal("100000")));

            boolean result = nhapHangBUS.createImport(null, items);
            assertFalseResult("Tao phieu voi pn=null", "PhieuNhap=null", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do Danh sách chi tiết rỗng")
        void testCreate_EmptyItems_ReturnsFalse() {
            // Đã sửa: Chỉ truyền 6 tham số đúng chuẩn của PhieuNhapDTO
            PhieuNhapDTO pn = new PhieuNhapDTO("PN01", "NV01", "NCC01", LocalDate.now(), 0, BigDecimal.ZERO);
            boolean result = nhapHangBUS.createImport(pn, new ArrayList<>());
            assertFalseResult("Tao phieu voi items rong", "items.size=0", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do Thiếu Mã Nhà Cung Cấp")
        void testCreate_EmptyMaNCC_ReturnsFalse() {
            PhieuNhapDTO pn = new PhieuNhapDTO("PN01", "NV01", "", LocalDate.now(), 0, BigDecimal.ZERO);
            List<ChiTietPhieuNhapDTO> items = new ArrayList<>();
            items.add(new ChiTietPhieuNhapDTO("PN01", "SP01", 10, new BigDecimal("100000")));

            boolean result = nhapHangBUS.createImport(pn, items);
            assertFalseResult("Tao phieu thieu MaNCC", "MaNCC=''", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do Số lượng sản phẩm <= 0")
        void testCreate_InvalidQuantity_ReturnsFalse() {
            PhieuNhapDTO pn = new PhieuNhapDTO("PN01", "NV01", "NCC01", LocalDate.now(), 0, BigDecimal.ZERO);
            List<ChiTietPhieuNhapDTO> items = new ArrayList<>();
            items.add(new ChiTietPhieuNhapDTO("PN01", "SP01", 0, new BigDecimal("100000"))); // Số lượng = 0

            boolean result = nhapHangBUS.createImport(pn, items);
            assertFalseResult("Tao phieu sp co so luong <= 0", "SoLuong=0", result);
        }

        @Test
        @DisplayName("Lập phiếu thất bại do Giá nhập bị âm")
        void testCreate_NegativePrice_ReturnsFalse() {
            PhieuNhapDTO pn = new PhieuNhapDTO("PN01", "NV01", "NCC01", LocalDate.now(), 0, BigDecimal.ZERO);
            List<ChiTietPhieuNhapDTO> items = new ArrayList<>();
            items.add(new ChiTietPhieuNhapDTO("PN01", "SP01", 10, new BigDecimal("-100000"))); // Giá âm

            boolean result = nhapHangBUS.createImport(pn, items);
            assertFalseResult("Tao phieu sp co gia nhap < 0", "GiaNhap=-100000", result);
        }
    }
}