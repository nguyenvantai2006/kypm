package com.qlgiay.bus;

import com.qlgiay.dto.QuyenDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp QuyenBUS (Có Output Console)")
class QuyenBUSTest {

    private QuyenBUS quyenBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[QuyenBUSTest] %s | input=%s | expected=false | actual=%b%n",
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
        quyenBUS = new QuyenBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM isValid() & addQuyen()
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Thêm Quyền Mới")
    class AddQuyenTests {

        @Test
        @DisplayName("Thêm thất bại do đối tượng null")
        void testAdd_NullObject_ReturnsFalse() {
            boolean result = quyenBUS.addQuyen(null);
            assertFalseResult("Them Quyen null", "q=null", result);
        }

        @Test
        @DisplayName("Thêm thất bại do bỏ trống Mã Quyền")
        void testAdd_EmptyMaQuyen_ReturnsFalse() {
            // Đã sửa: Truyền đủ 11 tham số (2 chuỗi, 9 int cho các quyền)
            QuyenDTO q = new QuyenDTO("", "Quyen Test", 1, 0, 0, 0, 0, 0, 0, 0, 0);
            boolean result = quyenBUS.addQuyen(q);
            assertFalseResult("Them Quyen thieu MaQuyen", "MaQuyen=''", result);
        }

        @Test
        @DisplayName("Thêm thất bại do bỏ trống Tên Quyền")
        void testAdd_EmptyTenQuyen_ReturnsFalse() {
            // Đã sửa: Truyền đủ 11 tham số
            QuyenDTO q = new QuyenDTO("Q01", "", 1, 0, 0, 0, 0, 0, 0, 0, 0);
            boolean result = quyenBUS.addQuyen(q);
            assertFalseResult("Them Quyen thieu TenQuyen", "TenQuyen=''", result);
        }
    }
}