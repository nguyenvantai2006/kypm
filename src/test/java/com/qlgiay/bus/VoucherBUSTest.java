package com.qlgiay.bus;

import com.qlgiay.dto.VoucherDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp VoucherBUS (Có Output Console)")
class VoucherBUSTest {

    private VoucherBUS voucherBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[VoucherBUSTest] %s | input=%s | expected=false | actual=%b%n",
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
        voucherBUS = new VoucherBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM isValid() & addVoucher()
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Thêm Voucher (Validate)")
    class AddVoucherTests {

        private VoucherDTO createValidBaseVoucher() {
            VoucherDTO v = new VoucherDTO();
            v.setMaVoucher("VOUCHER_TEST");
            v.setTenVoucher("Khuyen mai 10%");
            v.setPhanTramGiam(10);
            v.setGiamToiDa(new BigDecimal("50000"));
            v.setDieuKienApDung(new BigDecimal("200000"));
            v.setNgayBatDau(LocalDate.now());
            v.setNgayKetThuc(LocalDate.now().plusDays(10));
            v.setSoLuong(100);
            v.setTrangThai(1);
            return v;
        }

        @Test
        @DisplayName("Thêm thất bại do Voucher vừa không có % giảm vừa không có tiền giảm trực tiếp")
        void testAdd_NoDiscountValue_ReturnsFalse() {
            VoucherDTO v = createValidBaseVoucher();
            v.setPhanTramGiam(0);
            v.setSoTienGiam(BigDecimal.ZERO);

            boolean result = voucherBUS.addVoucher(v);
            assertFalseResult("Voucher khong co gia tri giam", "PhanTram=0, TienGiam=0", result);
        }

        @Test
        @DisplayName("Thêm thất bại do Ngày Kết Thúc nhỏ hơn Ngày Bắt Đầu")
        void testAdd_EndDateBeforeStartDate_ReturnsFalse() {
            VoucherDTO v = createValidBaseVoucher();
            v.setNgayBatDau(LocalDate.now());
            v.setNgayKetThuc(LocalDate.now().minusDays(5)); // Lỗi thời gian

            boolean result = voucherBUS.addVoucher(v);
            assertFalseResult("Ngay Ket Thuc truoc Ngay Bat Dau", "NgayKetThuc < NgayBatDau", result);
        }

        @Test
        @DisplayName("Thêm thất bại do có % Giảm nhưng thiếu số Tiền Giảm Tối Đa")
        void testAdd_PercentWithoutMaxDiscount_ReturnsFalse() {
            VoucherDTO v = createValidBaseVoucher();
            v.setPhanTramGiam(20);
            v.setGiamToiDa(BigDecimal.ZERO); // Lỗi logic

            boolean result = voucherBUS.addVoucher(v);
            assertFalseResult("Co phan tram nhung thieu giam toi da", "PhanTram>0, GiamToiDa=0", result);
        }

        @Test
        @DisplayName("Thêm thất bại do số lượng Voucher < 0")
        void testAdd_NegativeQuantity_ReturnsFalse() {
            VoucherDTO v = createValidBaseVoucher();
            v.setSoLuong(-10);

            boolean result = voucherBUS.addVoucher(v);
            assertFalseResult("So luong voucher am", "SoLuong=-10", result);
        }
    }
}