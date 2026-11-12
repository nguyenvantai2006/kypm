package com.qlgiay.bus;

import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp NhaCungCapBUS (Có Output Console)")
class NhaCungCapBUSTest {

    private NhaCungCapBUS nhaCungCapBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual) {
        System.out.printf("[NhaCungCapBUSTest] %s | input=%s | expected=false | actual=%b%n",
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
        nhaCungCapBUS = new NhaCungCapBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM isValid() & addSupplier()
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Thêm Nhà Cung Cấp")
    class AddSupplierTests {

        @Test
        @DisplayName("Thêm thất bại do đối tượng null")
        void testAdd_NullObject_ReturnsFalse() {
            boolean result = nhaCungCapBUS.addSupplier(null);
            assertFalseResult("Them NCC null", "ncc=null", result);
        }

        @Test
        @DisplayName("Thêm thất bại do bỏ trống Mã Nhà Cung Cấp")
        void testAdd_EmptyMaNCC_ReturnsFalse() {
            NhaCungCapDTO ncc = new NhaCungCapDTO("", "NCC Test", "0987654321", "Dia chi test", 1);
            boolean result = nhaCungCapBUS.addSupplier(ncc);
            assertFalseResult("Them NCC thieu MaNCC", "MaNCC=''", result);
        }

        @Test
        @DisplayName("Thêm thất bại do Số điện thoại không bắt đầu bằng số 0")
        void testAdd_InvalidPhonePrefix_ReturnsFalse() {
            NhaCungCapDTO ncc = new NhaCungCapDTO("NCC01", "NCC Test", "8498765432", "Dia chi test", 1);
            boolean result = nhaCungCapBUS.addSupplier(ncc);
            assertFalseResult("Them NCC sdt khong bat dau bang 0", "Sdt='8498765432'", result);
        }

        @Test
        @DisplayName("Thêm thất bại do Số điện thoại không đủ 10 số")
        void testAdd_InvalidPhoneLength_ReturnsFalse() {
            NhaCungCapDTO ncc = new NhaCungCapDTO("NCC01", "NCC Test", "098765", "Dia chi test", 1);
            boolean result = nhaCungCapBUS.addSupplier(ncc);
            assertFalseResult("Them NCC sdt thieu so", "Sdt='098765'", result);
        }
    }

    // ==========================================
    // NHÓM 2. TEST HÀM XÓA/KHÔI PHỤC
    // ==========================================
    @Nested
    @DisplayName("2. Chức năng Xóa/Khôi Phục Nhà Cung Cấp")
    class DeleteRestoreTests {

        @Test
        @DisplayName("Xóa thất bại do Mã NCC rỗng")
        void testDelete_EmptyMaNCC_ReturnsFalse() {
            boolean result = nhaCungCapBUS.deleteSupplier("");
            assertFalseResult("Xoa mem NCC thieu MaNCC", "MaNCC=''", result);
        }

        @Test
        @DisplayName("Khôi phục thất bại do Mã NCC rỗng")
        void testRestore_EmptyMaNCC_ReturnsFalse() {
            boolean result = nhaCungCapBUS.restoreSupplier("");
            assertFalseResult("Khoi phuc NCC thieu MaNCC", "MaNCC=''", result);
        }
    }
}