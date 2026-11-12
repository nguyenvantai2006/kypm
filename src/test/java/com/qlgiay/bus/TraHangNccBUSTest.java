package com.qlgiay.bus;

import com.qlgiay.dto.ChiTietTraNccDTO;
import com.qlgiay.dto.PhieuTraNccDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp TraHangNccBUS (Có Output Console)")
class TraHangNccBUSTest {

    private TraHangNccBUS traHangNccBUS;

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertFalseResult(String testCase, String input, boolean actual, String errorMessage) {
        System.out.printf("[TraHangNccBUSTest] %s | input=%s | expected=false | actual=%b | error=%s%n",
                testCase, input, actual, errorMessage);
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
        traHangNccBUS = new TraHangNccBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    // ==========================================
    // NHÓM 1. TEST HÀM createRequest() (Yêu cầu trả hàng)
    // ==========================================
    @Nested
    @DisplayName("1. Chức năng Lập Phiếu Trả Hàng NCC")
    class CreateRequestTests {

        @Test
        @DisplayName("Lập phiếu thất bại do Đối tượng bị Null")
        void testCreate_NullObject_ReturnsFalse() {
            boolean result = traHangNccBUS.createRequest(null, null);
            assertFalseResult("Tao phieu tra NCC null", "phieu=null, chiTiet=null", result,
                    traHangNccBUS.getLastError());
        }

        @Test
        @DisplayName("Lập phiếu thất bại do thiếu Mã Phiếu Nhập gốc")
        void testCreate_MissingMaPN_ReturnsFalse() {
            PhieuTraNccDTO phieu = new PhieuTraNccDTO();
            phieu.setMaPT("PT01");
            phieu.setMaNV("NV01");
            phieu.setMaNCC("NCC01");

            ChiTietTraNccDTO chiTiet = new ChiTietTraNccDTO("PT01", "SP01", 5, BigDecimal.ZERO);

            boolean result = traHangNccBUS.createRequest(phieu, chiTiet);
            assertFalseResult("Tao phieu thieu MaPN goc", "MaPN=null", result, traHangNccBUS.getLastError());
        }

        @Test
        @DisplayName("Lập phiếu thất bại do Số lượng trả <= 0")
        void testCreate_InvalidQuantity_ReturnsFalse() {
            PhieuTraNccDTO phieu = new PhieuTraNccDTO();
            phieu.setMaPT("PT01");
            phieu.setMaPN("PN01");
            phieu.setMaNV("NV01");
            phieu.setMaNCC("NCC01");

            ChiTietTraNccDTO chiTiet = new ChiTietTraNccDTO("PT01", "SP01", 0, BigDecimal.ZERO); // Số lượng = 0

            boolean result = traHangNccBUS.createRequest(phieu, chiTiet);
            assertFalseResult("Tao phieu so luong khong hop le", "SoLuong=0", result, traHangNccBUS.getLastError());
        }
    }
}