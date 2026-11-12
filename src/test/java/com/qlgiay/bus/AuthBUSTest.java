package com.qlgiay.bus;

import com.qlgiay.dto.AuthSession;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp AuthBUS (Có Output Console)")
class AuthBUSTest {

    private AuthBUS authBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertNullResult(String testCase, String input, Object actual) {
        System.out.printf("[AuthBUSTest] %s | input=%s | expected=null | actual=%s%n",
                testCase, input, actual);
        assertNull(actual);
    }

    private void assertNotNullResult(String testCase, String input, Object actual) {
        System.out.printf("[AuthBUSTest] %s | input=%s | expected=NotNull | actual=%s%n",
                testCase, input, actual != null ? "Object Created" : "null");
        assertNotNull(actual);
    }

    // ==========================================
    // QUẢN LÝ VÒNG ĐỜI BÀI TEST (LIFECYCLE)
    // ==========================================

    @BeforeEach
    void setUp() {
        authBUS = new AuthBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM login() (Dựa trên UC-01)
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Đăng nhập (UC-01)")
    class LoginTests {

        @Test
        @DisplayName("Đăng nhập thất bại do bỏ trống Tài khoản (E1)")
        void testLogin_EmptyUsername_ReturnsNull() {
            AuthSession result = authBUS.login("", "123456");
            assertNullResult("login voi username rong", "username=''", result);
        }

        @Test
        @DisplayName("Đăng nhập thất bại do bỏ trống Mật khẩu (E1)")
        void testLogin_EmptyPassword_ReturnsNull() {
            AuthSession result = authBUS.login("admin", "");
            assertNullResult("login voi password rong", "password=''", result);
        }

        @Test
        @DisplayName("Đăng nhập thất bại do chứa ký tự đặc biệt không hợp lệ (E1)")
        void testLogin_SpecialChars_ReturnsNull() {
            AuthSession result = authBUS.login("admin' OR '1'='1", "123456");
            assertNullResult("login chua SQL Injection char", "username='admin\\' OR \\'1\\'=\\'1'", result);
        }

        @Test
        @DisplayName("Đăng nhập thất bại do sai Thông tin tài khoản/mật khẩu (E2)")
        void testLogin_WrongCredentials_ReturnsNull() {
            // Giả định "admin" và "wrongpass" không khớp DB
            AuthSession result = authBUS.login("admin", "wrongpass");
            assertNullResult("login sai mat khau", "password='wrongpass'", result);
        }

        @Test
        @DisplayName("Đăng nhập thất bại do Tài khoản bị khóa (E3)")
        void testLogin_AccountLocked_ReturnsNull() {
            // Giả định "lockedUser" là tài khoản có trạng thái khóa trong DB test
            AuthSession result = authBUS.login("lockedUser", "123456");
            assertNullResult("login tai khoan bi khoa", "username='lockedUser'", result);
        }

        @Test
        @DisplayName("Đăng nhập thành công (Happy Path)")
        void testLogin_Success_ReturnsAuthSession() {
            // Giả định "admin" và "123456" là tài khoản hợp lệ trong DB test
            AuthSession result = authBUS.login("admin", "123456");

            // Chỉ cần kiểm tra khởi tạo phiên thành công (khác null) là đủ
            assertNotNullResult("login thanh cong", "username='admin'", result);
        }
    }
}