package com.qlgiay.bus;

import com.qlgiay.dto.NhanVienDTO;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")

@DisplayName("Kiểm thử nghiệp vụ lớp NhanVienBUS (Có Output Console)")
class NhanVienBUSTest {

    private NhanVienBUS nhanVienBUS;

    
    private void assertBooleanResult(String testCase, String input, boolean expected, boolean actual) {
        System.out.printf("[NhanVienBUSTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    private void assertNullResult(String testCase, String input, Object actual) {
        System.out.printf("[NhanVienBUSTest] %s | input=%s | expected=null | actual=%s%n",
                testCase, input, actual);
        assertNull(actual);
    }

    
    @BeforeAll
    static void initAll() {
      
    }

    @AfterAll
    static void tearDownAll() {
       
    }

    @BeforeEach
    void setUp() {
       
        nhanVienBUS = new NhanVienBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
     
    }

   
    @Nested
    @DisplayName("1. Chức năng Thêm Nhân Viên")
    class AddNhanVienTests {

        @Test
        @DisplayName("Thêm thất bại do truyền vào Null")
        void testAddNhanVien_NullInput_ReturnsFalse() {
            boolean result = nhanVienBUS.addNhanVien(null);
            assertBooleanResult("addNhanVien voi null", "null", false, result);
        }

        @Test
        @DisplayName("Thêm thất bại do Mã NV rỗng")
        void testAddNhanVien_EmptyFields_ReturnsFalse() {
            NhanVienDTO nv = new NhanVienDTO();
            nv.setMaNV(""); // Rỗng
            nv.setHo("Nguyễn");
            nv.setTen("Văn A");
            nv.setMaQuyen("Q01");
            nv.setTaiKhoan("nva");
            nv.setMatKhau("123456");

            boolean result = nhanVienBUS.addNhanVien(nv);
            assertBooleanResult("addNhanVien voi ma rong", "maNV=''", false, result);
        }

        @Test
        @DisplayName("Thêm thất bại do Tài khoản chứa khoảng trắng")
        void testAddNhanVien_TaiKhoanChuaKhoangTrang_ReturnsFalse() {
            NhanVienDTO nv = new NhanVienDTO();
            nv.setMaNV("NV01");
            nv.setHo("Nguyễn");
            nv.setTen("A");
            nv.setMaQuyen("Q01");
            nv.setTaiKhoan("admin 123"); // Chứa khoảng trắng
            nv.setMatKhau("123456");

            boolean result = nhanVienBUS.addNhanVien(nv);
            assertBooleanResult("addNhanVien voi tai khoan co khoang trang",
                    "taiKhoan='admin 123'", false, result);
        }

        @Test
        @DisplayName("Thêm thất bại do Mật khẩu ngắn hơn 6 ký tự")
        void testAddNhanVien_MatKhauNgan_ReturnsFalse() {
            NhanVienDTO nv = new NhanVienDTO();
            nv.setMaNV("NV01");
            nv.setHo("Nguyễn");
            nv.setTen("A");
            nv.setMaQuyen("Q01");
            nv.setTaiKhoan("admin");
            nv.setMatKhau("12345"); // < 6 ký tự

            boolean result = nhanVienBUS.addNhanVien(nv);
            assertBooleanResult("addNhanVien voi mat khau qua ngan",
                    "do dai mat khau=5", false, result);
        }

        @Test
        @DisplayName("Thêm thất bại do Mức lương là số âm")
        void testAddNhanVien_LuongAm_ReturnsFalse() {
            NhanVienDTO nv = new NhanVienDTO();
            nv.setMaNV("NV01");
            nv.setHo("Nguyễn");
            nv.setTen("A");
            nv.setMaQuyen("Q01");
            nv.setTaiKhoan("admin");
            nv.setMatKhau("123456");
            nv.setLuong(new BigDecimal("-1000")); // Lương âm

            boolean result = nhanVienBUS.addNhanVien(nv);
            assertBooleanResult("addNhanVien voi luong am", "luong=-1000", false, result);
        }
    }

    @Nested
    @DisplayName("2. Chức năng Cập nhật Nhân Viên")
    class UpdateNhanVienTests {

        @Test
        @DisplayName("Cập nhật thất bại do truyền vào Null")
        void testUpdateNhanVien_NullInput_ReturnsFalse() {
            boolean result = nhanVienBUS.updateNhanVien(null);
            assertBooleanResult("updateNhanVien voi null", "null", false, result);
        }

        @Test
        @DisplayName("Cập nhật thất bại do Mật khẩu mới quá ngắn")
        void testUpdateNhanVien_InvalidPasswordLength_ReturnsFalse() {
            NhanVienDTO nv = new NhanVienDTO();
            nv.setMaNV("NV01");
            nv.setHo("Nguyễn");
            nv.setTen("A");
            nv.setMaQuyen("Q01");
            nv.setTaiKhoan("admin");
            nv.setMatKhau("123"); // Quá ngắn

            boolean result = nhanVienBUS.updateNhanVien(nv);
            assertBooleanResult("updateNhanVien voi mat khau qua ngan",
                    "maNV='NV01', do dai mat khau=3", false, result);
        }
    }

    
    @Nested
    @DisplayName("3. Chức năng Tìm kiếm và Khóa/Mở Khóa")
    class SearchAndLockTests {

        @Test
        @DisplayName("Tìm ID thất bại do chỉ chứa khoảng trắng")
        void testFindById_EmptyId_ReturnsNull() {
            NhanVienDTO result = nhanVienBUS.findById("   ");
            assertNullResult("findById voi ID chi co khoang trang", "maNV='   '", result);
        }

        @Test
        @DisplayName("Tìm Tài Khoản thất bại do chuỗi rỗng")
        void testFindByTaiKhoan_EmptyTk_ReturnsNull() {
            NhanVienDTO result = nhanVienBUS.findByTaiKhoan("");
            assertNullResult("findByTaiKhoan voi tai khoan rong", "taiKhoan=''", result);
        }

        @Test
        @DisplayName("Khóa nhân viên thất bại do Mã NV rỗng")
        void testLockNhanVien_EmptyId_ReturnsFalse() {
            boolean result = nhanVienBUS.lockNhanVien("");
            assertBooleanResult("lockNhanVien voi ID rong", "maNV=''", false, result);
        }

        @Test
        @DisplayName("Mở khóa nhân viên thất bại do Mã NV Null")
        void testUnlockNhanVien_EmptyId_ReturnsFalse() {
            boolean result = nhanVienBUS.unlockNhanVien(null);
            assertBooleanResult("unlockNhanVien voi ID null", "maNV=null", false, result);
        }
    }
}