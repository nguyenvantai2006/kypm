package com.qlgiay.bus;

import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp KhachHangBUS")
class KhachHangBUSTest {

    private KhachHangBUS khachHangBUS;
    private static final String TEST_KH_ID = "KH99999";
    private static final String TEST_PHONE = "0999999999";

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertBooleanResult(String testCase, String input, boolean expected, boolean actual) {
        System.out.printf("[KhachHangBUSTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    private void assertNullResult(String testCase, String input, Object actual) {
        System.out.printf("[KhachHangBUSTest] %s | input=%s | expected=null | actual=%s%n",
                testCase, input, actual == null ? "null" : "NotNull");
        assertNull(actual);
    }

    private void assertNotNullResult(String testCase, String input, Object actual) {
        System.out.printf("[KhachHangBUSTest] %s | input=%s | expected=NotNull | actual=%s%n",
                testCase, input, actual != null ? "NotNull" : "null");
        assertNotNull(actual);
    }

    // ==========================================
    // QUẢN LÝ VÒNG ĐỜI TOÀN FILE
    // ==========================================
    @BeforeAll
    static void initAll() {
        try {
            DBConnect.enableTestConnection();
        } catch (Exception ignored) {
        }
        hardDeleteTestRecordStatic(); // Dọn DB trước khi chạy nguyên file
    }

    @AfterAll
    static void tearDownAll() {
        hardDeleteTestRecordStatic(); // Test xong nguyên file thì dọn rác trả lại DB sạch
        DBConnect.closeTestConnection();
    }

    private static void hardDeleteTestRecordStatic() {
        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM KHACH_HANG WHERE MaKH = ?")) {
            ps.setString(1, TEST_KH_ID);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        khachHangBUS = new KhachHangBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    private KhachHangDTO validCustomer() {
        return new KhachHangDTO(TEST_KH_ID, "Lê Phát Triển", TEST_PHONE, "Đồng Nai", 0, 1);
    }

    // ==========================================
    // NHÓM 1, 2, 3: CÁC RÀNG BUỘC KIỂM TRA DỮ LIỆU ĐẦU VÀO
    // ==========================================
    @Nested
    @DisplayName("1. Kiểm tra xác thực dữ liệu (Thêm và Cập nhật)")
    class ValidationTests {
        @Test
        @DisplayName("Thêm thất bại do khách hàng Null")
        void testAdd_NullCustomer() {
            assertBooleanResult("VAL-01 Them voi khach hang null", "kh=null", false, khachHangBUS.addCustomer(null));
        }

        @Test
        @DisplayName("Thêm thất bại do Mã khách hàng rỗng")
        void testAdd_EmptyId() {
            KhachHangDTO kh = validCustomer();
            kh.setMaKH("  ");
            assertBooleanResult("VAL-02 Them voi maKH rong", "maKH='  '", false, khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Thêm thất bại do Tên chứa số")
        void testAdd_NameContainsNumber() {
            KhachHangDTO kh = validCustomer();
            kh.setTenKH("Lê Phát Triển 99");
            assertBooleanResult("VAL-03 Them voi tenKH chua so", "tenKH='Lê Phát Triển 99'", false,
                    khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Thêm thất bại do Tên chứa ký tự đặc biệt")
        void testAdd_NameContainsSpecial() {
            KhachHangDTO kh = validCustomer();
            kh.setTenKH("Lê Phát @Triển");
            assertBooleanResult("VAL-04 Them voi tenKH chua ky tu dac biet", "tenKH='Lê Phát @Triển'", false,
                    khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Thêm thất bại do SĐT không bắt đầu bằng số 0")
        void testAdd_PhoneNotStartingWithZero() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("8499999999");
            assertBooleanResult("VAL-05 Them voi sdt khong bat dau bang 0", "sdt='8499999999'", false,
                    khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Thêm thất bại do SĐT thiếu số (< 10 số)")
        void testAdd_PhoneTooShort() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("099999999");
            assertBooleanResult("VAL-06 Them voi sdt thieu so (9 so)", "sdt='099999999'", false,
                    khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Thêm thất bại do SĐT dư số (> 10 số)")
        void testAdd_PhoneTooLong() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("09999999991");
            assertBooleanResult("VAL-07 Them voi sdt du so (11 so)", "sdt='09999999991'", false,
                    khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Thêm thất bại do SĐT chứa chữ cái")
        void testAdd_PhoneContainsLetters() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("099999999a");
            assertBooleanResult("VAL-08 Them voi sdt chua chu cai", "sdt='099999999a'", false,
                    khachHangBUS.addCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do khách hàng Null")
        void testUpdate_NullCustomer() {
            assertBooleanResult("VAL-09 Cap nhat voi khach hang null", "kh=null", false,
                    khachHangBUS.updateCustomer(null));
        }

        @Test
        @DisplayName("Cập nhật thất bại do Mã khách hàng rỗng")
        void testUpdate_EmptyId() {
            KhachHangDTO kh = validCustomer();
            kh.setMaKH("  ");
            assertBooleanResult("VAL-10 Cap nhat voi maKH rong", "maKH='  '", false, khachHangBUS.updateCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do Tên chứa số")
        void testUpdate_NameContainsNumber() {
            KhachHangDTO kh = validCustomer();
            kh.setTenKH("Lê Phát Triển 99");
            assertBooleanResult("VAL-11 Cap nhat voi tenKH chua so", "tenKH='Lê Phát Triển 99'", false,
                    khachHangBUS.updateCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do Tên chứa ký tự đặc biệt")
        void testUpdate_NameContainsSpecial() {
            KhachHangDTO kh = validCustomer();
            kh.setTenKH("Lê Phát Triển @#$");
            assertBooleanResult("VAL-12 Cap nhat voi tenKH chua ky tu dac biet", "tenKH='Lê Phát Triển @#$'", false,
                    khachHangBUS.updateCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do SĐT không bắt đầu bằng số 0")
        void testUpdate_PhoneDoesNotStartWithZero() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("9999999999");
            assertBooleanResult("VAL-13 Cap nhat voi sdt khong bat dau bang so 0", "sdt='9999999999'", false,
                    khachHangBUS.updateCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do SĐT thiếu số (< 10 số)")
        void testUpdate_PhoneTooShort() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("099999999");
            assertBooleanResult("VAL-14 Cap nhat voi sdt thieu so (9 so)", "sdt='099999999'", false,
                    khachHangBUS.updateCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do SĐT dư số (> 10 số)")
        void testUpdate_PhoneTooLong() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("09999999991");
            assertBooleanResult("VAL-15 Cap nhat voi sdt du so (11 so)", "sdt='09999999991'", false,
                    khachHangBUS.updateCustomer(kh));
        }

        @Test
        @DisplayName("Cập nhật thất bại do SĐT chứa chữ cái")
        void testUpdate_PhoneContainsLetters() {
            KhachHangDTO kh = validCustomer();
            kh.setSdt("099999999a");
            assertBooleanResult("VAL-16 Cap nhat voi sdt chua chu cai", "sdt='099999999a'", false,
                    khachHangBUS.updateCustomer(kh));
        }
    }

    @Nested
    @DisplayName("2. Kiểm tra lỗi thao tác Xóa và Khôi phục")
    class ActionValidationTests {
        @Test
        void testDelete_NullId() {
            assertBooleanResult("ACT-01 Xoa mem voi maKH null", "maKH=null", false, khachHangBUS.deleteCustomer(null));
        }

        @Test
        void testDelete_EmptyId() {
            assertBooleanResult("ACT-02 Xoa mem voi maKH rong", "maKH='   '", false,
                    khachHangBUS.deleteCustomer("   "));
        }

        @Test
        void testDelete_NotFoundId() {
            assertBooleanResult("ACT-03 Xoa mem voi maKH khong ton tai", "maKH='KH991'", false,
                    khachHangBUS.deleteCustomer("KH991"));
        }

        @Test
        void testRestore_NullId() {
            assertBooleanResult("ACT-04 Khoi phuc voi maKH null", "maKH=null", false,
                    khachHangBUS.restoreCustomer(null));
        }

        @Test
        void testRestore_EmptyId() {
            assertBooleanResult("ACT-05 Khoi phuc voi maKH rong", "maKH='   '", false,
                    khachHangBUS.restoreCustomer("   "));
        }

        @Test
        void testRestore_NotFoundId() {
            assertBooleanResult("ACT-06 Khoi phuc voi maKH khong ton tai", "maKH='KH991'", false,
                    khachHangBUS.restoreCustomer("KH991"));
        }
    }

    @Nested
    @DisplayName("3. Kiểm tra lỗi thao tác Tìm kiếm")
    class SearchValidationTests {
        @Test
        void testFindById_Null() {
            assertNullResult("SRCH-01 Tim theo ID voi maKH null", "maKH=null", khachHangBUS.findById(null));
        }

        @Test
        void testFindById_Empty() {
            assertNullResult("SRCH-02 Tim theo ID voi maKH rong", "maKH='   '", khachHangBUS.findById("   "));
        }

        @Test
        void testFindById_NotFound() {
            assertNullResult("SRCH-03 Tim theo ID voi maKH khong ton tai", "maKH='KH991'",
                    khachHangBUS.findById("KH991"));
        }

        @Test
        void testFindByPhone_Null() {
            assertNullResult("SRCH-04 Tim theo SDT voi sdt null", "sdt=null", khachHangBUS.findByPhone(null));
        }

        @Test
        void testFindByPhone_Empty() {
            assertNullResult("SRCH-05 Tim theo SDT voi sdt rong", "sdt='   '", khachHangBUS.findByPhone("   "));
        }

        @Test
        void testFindByPhone_NotFound() {
            assertNullResult("SRCH-06 Tim theo SDT voi sdt khong ton tai", "sdt='0999999991'",
                    khachHangBUS.findByPhone("0999999991"));
        }
    }

    // ==========================================
    // NHÓM 4: LUỒNG CHẠY THỰC TẾ (STATEFUL TESTING)
    // ==========================================
    @Nested
    @DisplayName("4. Kiểm thử Luồng Vòng Đời Khách Hàng")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ép chạy theo thứ tự các bước
    class LifecycleTests {

        @Test
        @Order(1)
        @DisplayName("Bước 1: Thêm khách hàng mới")
        void step1_Insert() {
            boolean result = khachHangBUS.addCustomer(validCustomer());
            assertBooleanResult("LC-01 B1: Them khach hang hop le", "kh=" + TEST_KH_ID, true, result);
        }

        @Test
        @Order(2)
        @DisplayName("Bước 2: Tìm kiếm người vừa thêm")
        void step2_Search() {
            KhachHangDTO found = khachHangBUS.findById(TEST_KH_ID);
            assertNotNullResult("LC-02 B2: Tim nguoi vua them tren DB", "maKH=" + TEST_KH_ID, found);
            assertEquals("Lê Phát Triển", found.getTenKH());
        }

        @Test
        @Order(3)
        @DisplayName("Bước 3: Chỉnh sửa thông tin")
        void step3_Update() {
            KhachHangDTO kh = validCustomer();
            kh.setDiaChi("Sài Gòn"); // Đổi địa chỉ
            boolean result = khachHangBUS.updateCustomer(kh);
            assertBooleanResult("LC-03 B3: Chinh sua thong tin", "DiaChi='Sài Gòn'", true, result);
        }

        @Test
        @Order(4)
        @DisplayName("Bước 4: Tìm đúng người và Xóa")
        void step4_DeleteReal() {
            boolean result = khachHangBUS.deleteCustomer(TEST_KH_ID);
            assertBooleanResult("LC-04 B4: Tim dung nguoi va Xoa mem", "maKH=" + TEST_KH_ID, true, result);
        }
    }
}