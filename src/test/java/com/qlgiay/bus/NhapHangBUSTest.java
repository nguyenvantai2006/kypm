package com.qlgiay.bus;

import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.dto.PhieuNhapDTO;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("BusinessLogicTest")
@DisplayName("Kiểm thử nghiệp vụ lớp NhapHangBUS (Có Output Console)")
class NhapHangBUSTest {

    private NhapHangBUS nhapHangBUS;

    private void assertBooleanResult(String testCase, String input, boolean expected, boolean actual) {
        System.out.printf("[NhapHangBUSTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeAll
    static void initAll() {
    }

    @AfterAll
    static void tearDownAll() {
    }

    @BeforeEach
    void setUp() {
        nhapHangBUS = new NhapHangBUS();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
    }

    private PhieuNhapDTO validImport() {
        PhieuNhapDTO phieuNhap = new PhieuNhapDTO();
        phieuNhap.setMaPN("PN01");
        phieuNhap.setMaNV("NV01");
        phieuNhap.setMaNCC("NCC01");
        return phieuNhap;
    }

    private ChiTietPhieuNhapDTO validItem() {
        return new ChiTietPhieuNhapDTO("PN01", "SP01", 2, new BigDecimal("10000"));
    }

    @Nested
    @DisplayName("1. Kiểm tra dữ liệu phiếu nhập")
    class ImportValidationTests {

        @Test
        @DisplayName("Tạo thất bại do phiếu nhập Null")
        void testCreateImport_NullImport_ReturnsFalse() {
            boolean result = nhapHangBUS.createImport(null, List.of(validItem()));
            assertBooleanResult("createImport voi phieu nhap null", "phieuNhap=null", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do danh sách mặt hàng Null")
        void testCreateImport_NullItems_ReturnsFalse() {
            boolean result = nhapHangBUS.createImport(validImport(), null);
            assertBooleanResult("createImport voi danh sach null", "items=null", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do danh sách mặt hàng rỗng")
        void testCreateImport_EmptyItems_ReturnsFalse() {
            boolean result = nhapHangBUS.createImport(validImport(), List.of());
            assertBooleanResult("createImport voi danh sach rong", "items=[]", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do Mã phiếu nhập Null")
        void testCreateImport_NullImportId_ReturnsFalse() {
            PhieuNhapDTO phieuNhap = validImport();
            phieuNhap.setMaPN(null);

            boolean result = nhapHangBUS.createImport(phieuNhap, List.of(validItem()));
            assertBooleanResult("createImport voi maPN null", "maPN=null", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do Mã phiếu nhập rỗng")
        void testCreateImport_EmptyImportId_ReturnsFalse() {
            PhieuNhapDTO phieuNhap = validImport();
            phieuNhap.setMaPN("  ");

            boolean result = nhapHangBUS.createImport(phieuNhap, List.of(validItem()));
            assertBooleanResult("createImport voi maPN rong", "maPN='  '", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do Mã nhân viên rỗng")
        void testCreateImport_EmptyEmployeeId_ReturnsFalse() {
            PhieuNhapDTO phieuNhap = validImport();
            phieuNhap.setMaNV("");

            boolean result = nhapHangBUS.createImport(phieuNhap, List.of(validItem()));
            assertBooleanResult("createImport voi maNV rong", "maNV=''", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do Mã nhà cung cấp chỉ có khoảng trắng")
        void testCreateImport_BlankSupplierId_ReturnsFalse() {
            PhieuNhapDTO phieuNhap = validImport();
            phieuNhap.setMaNCC("   ");

            boolean result = nhapHangBUS.createImport(phieuNhap, List.of(validItem()));
            assertBooleanResult("createImport voi maNCC chi co khoang trang",
                    "maNCC='   '", false, result);
        }
    }

    @Nested
    @DisplayName("2. Kiểm tra chi tiết mặt hàng nhập")
    class ImportItemValidationTests {

        @Test
        @DisplayName("Tạo thất bại do chi tiết mặt hàng Null")
        void testCreateImport_NullItem_ReturnsFalse() {
            boolean result = nhapHangBUS.createImport(validImport(), java.util.Collections.singletonList(null));
            assertBooleanResult("createImport voi chi tiet null", "item=null", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do Mã sản phẩm rỗng")
        void testCreateImport_EmptyProductId_ReturnsFalse() {
            ChiTietPhieuNhapDTO item = validItem();
            item.setMaSP(" ");

            boolean result = nhapHangBUS.createImport(validImport(), List.of(item));
            assertBooleanResult("createImport voi maSP rong", "maSP=' '", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do số lượng bằng 0")
        void testCreateImport_ZeroQuantity_ReturnsFalse() {
            ChiTietPhieuNhapDTO item = validItem();
            item.setSoLuong(0);

            boolean result = nhapHangBUS.createImport(validImport(), List.of(item));
            assertBooleanResult("createImport voi soLuong bang 0", "soLuong=0", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do số lượng âm")
        void testCreateImport_NegativeQuantity_ReturnsFalse() {
            ChiTietPhieuNhapDTO item = validItem();
            item.setSoLuong(-1);

            boolean result = nhapHangBUS.createImport(validImport(), List.of(item));
            assertBooleanResult("createImport voi soLuong am", "soLuong=-1", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do giá nhập Null")
        void testCreateImport_NullPurchasePrice_ReturnsFalse() {
            ChiTietPhieuNhapDTO item = validItem();
            item.setGiaNhap(null);

            boolean result = nhapHangBUS.createImport(validImport(), List.of(item));
            assertBooleanResult("createImport voi giaNhap null", "giaNhap=null", false, result);
        }

        @Test
        @DisplayName("Tạo thất bại do giá nhập âm")
        void testCreateImport_NegativePurchasePrice_ReturnsFalse() {
            ChiTietPhieuNhapDTO item = validItem();
            item.setGiaNhap(new BigDecimal("-1"));

            boolean result = nhapHangBUS.createImport(validImport(), List.of(item));
            assertBooleanResult("createImport voi giaNhap am", "giaNhap=-1", false, result);
        }
    }
}