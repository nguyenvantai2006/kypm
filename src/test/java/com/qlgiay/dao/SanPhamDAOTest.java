package com.qlgiay.dao;

import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp SanPhamDAO (Tương tác CSDL thật)")
class SanPhamDAOTest {

    private SanPhamDAO sanPhamDAO;
    private final String TEST_SP_ID = "SP_TEST_99";
    private final String TEST_NCC_ID = "NCC_TEST_99";

    @BeforeAll
    static void initAll() {
        try {
            DBConnect.getConnection().close();
        } catch (Exception e) {
            fail("Không thể khởi tạo Test Connection: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
    }

    @BeforeEach
    void setUp() {
        sanPhamDAO = new SanPhamDAO();
        hardDeleteTestRecords();
        insertDummyNhaCungCap();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecords();
    }

    private void hardDeleteTestRecords() {
        try (Connection c = DBConnect.getConnection()) {
            try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM SAN_PHAM WHERE MaSP = ?")) {
                ps1.setString(1, TEST_SP_ID);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement("DELETE FROM NHA_CUNG_CAP WHERE MaNCC = ?")) {
                ps2.setString(1, TEST_NCC_ID);
                ps2.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    private void insertDummyNhaCungCap() {
        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO NHA_CUNG_CAP (MaNCC, TenNCC, TrangThai) VALUES (?, 'NCC Test', 1)")) {
            ps.setString(1, TEST_NCC_ID);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private SanPhamDTO createMockProduct() {
        SanPhamDTO sp = new SanPhamDTO(TEST_SP_ID, "Giày Test", "Sneaker", "Đôi", 100, new BigDecimal("1000000"),
                "Đen", "42", "Vải", "Nike", "Mỹ", LocalDate.now(), "Mô tả test", "test.jpg", 1);
        sp.setMaNCC(TEST_NCC_ID);
        sp.setPhanTramLoiNhuan(new BigDecimal("20"));
        return sp;
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Sản phẩm")
    void testInsertAndFind() {
        SanPhamDTO sp = createMockProduct();

        boolean isInserted = sanPhamDAO.insert(sp);
        assertTrue(isInserted, "Lệnh INSERT sản phẩm phải thành công");

        SanPhamDTO byId = sanPhamDAO.findById(TEST_SP_ID);
        assertNotNull(byId);
        assertEquals("Giày Test", byId.getTenSP());
        assertEquals(TEST_NCC_ID, byId.getMaNCC());
    }

    @Test
    @DisplayName("Cập nhật thông tin Sản phẩm")
    void testUpdate() {
        SanPhamDTO sp = createMockProduct();
        sanPhamDAO.insert(sp);

        sp.setTenSP("Giày Update");
        sp.setSoLuong(500);
        boolean isUpdated = sanPhamDAO.update(sp);

        assertTrue(isUpdated);
        SanPhamDTO updatedSP = sanPhamDAO.findById(TEST_SP_ID);
        assertEquals("Giày Update", updatedSP.getTenSP());
        assertEquals(500, updatedSP.getSoLuong());
    }

    @Test
    @DisplayName("Tăng và Giảm số lượng tồn kho (Bán hàng/Nhập hàng)")
    void testStockManagement() {
        sanPhamDAO.insert(createMockProduct());

        try (Connection c = DBConnect.getConnection()) {
            boolean decreased = sanPhamDAO.decreaseStock(c, TEST_SP_ID, 20);
            assertTrue(decreased);
            assertEquals(80, sanPhamDAO.findById(TEST_SP_ID).getSoLuong());

            boolean increased = sanPhamDAO.increaseStock(c, TEST_SP_ID, 50);
            assertTrue(increased);
            assertEquals(130, sanPhamDAO.findById(TEST_SP_ID).getSoLuong());

            boolean failDecrease = sanPhamDAO.decreaseStock(c, TEST_SP_ID, 200);
            assertFalse(failDecrease, "Phải false vì số lượng tồn kho không được âm");

        } catch (Exception e) {
            fail("Lỗi kết nối CSDL: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Xóa mềm và Khôi phục Sản phẩm")
    void testSoftDeleteAndRestore() {
        sanPhamDAO.insert(createMockProduct());

        boolean isDeleted = sanPhamDAO.softDelete(TEST_SP_ID);
        assertTrue(isDeleted);
        assertEquals(0, sanPhamDAO.findById(TEST_SP_ID).getTrangThai());
        assertNull(sanPhamDAO.findByIdActive(TEST_SP_ID), "Tìm kiếm Active phải ra null vì đã xóa mềm");

        boolean isRestored = sanPhamDAO.restore(TEST_SP_ID);
        assertTrue(isRestored);
        assertEquals(1, sanPhamDAO.findById(TEST_SP_ID).getTrangThai());
        assertNotNull(sanPhamDAO.findByIdActive(TEST_SP_ID));
    }
}