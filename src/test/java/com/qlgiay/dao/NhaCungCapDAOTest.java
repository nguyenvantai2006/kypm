package com.qlgiay.dao;

import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp NhaCungCapDAO (Tương tác CSDL thật)")
class NhaCungCapDAOTest {

    private NhaCungCapDAO nhaCungCapDAO;
    private final String TEST_NCC_ID = "NCC_TEST_99";

    @BeforeAll
    static void initAll() {
        try {
            DBConnect.getConnection().close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
    }

    @BeforeEach
    void setUp() {
        nhaCungCapDAO = new NhaCungCapDAO();
        hardDeleteTestRecord();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecord();
    }

    private void hardDeleteTestRecord() {
        try (Connection c = DBConnect.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM NHA_CUNG_CAP WHERE MaNCC = ?")) {
                ps.setString(1, TEST_NCC_ID);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Nhà cung cấp")
    void testInsertAndFind() {
        NhaCungCapDTO ncc = new NhaCungCapDTO(TEST_NCC_ID, "Công ty Test", "0999999999", "Địa chỉ Test", 1);

        boolean isInserted = nhaCungCapDAO.insert(ncc);
        assertTrue(isInserted, "Lệnh INSERT Nhà cung cấp phải thành công");

        NhaCungCapDTO found = nhaCungCapDAO.findById(TEST_NCC_ID);
        assertNotNull(found);
        assertEquals("Công ty Test", found.getTenNCC());
        assertEquals("0999999999", found.getSdt());
    }

    @Test
    @DisplayName("Cập nhật thông tin Nhà cung cấp")
    void testUpdate() {
        nhaCungCapDAO.insert(new NhaCungCapDTO(TEST_NCC_ID, "Tên Cũ", "0999999999", "Địa chỉ", 1));

        NhaCungCapDTO ncc = new NhaCungCapDTO(TEST_NCC_ID, "Tên Mới", "0123456789", "Địa chỉ mới", 1);
        boolean isUpdated = nhaCungCapDAO.update(ncc);
        assertTrue(isUpdated);

        NhaCungCapDTO updatedNCC = nhaCungCapDAO.findById(TEST_NCC_ID);
        assertEquals("Tên Mới", updatedNCC.getTenNCC());
        assertEquals("0123456789", updatedNCC.getSdt());
    }

    @Test
    @DisplayName("Xóa mềm và Khôi phục Nhà cung cấp")
    void testSoftDeleteAndRestore() {
        nhaCungCapDAO.insert(new NhaCungCapDTO(TEST_NCC_ID, "NCC Xóa Mềm", "0999999999", "Địa chỉ", 1));

        boolean isDeleted = nhaCungCapDAO.softDelete(TEST_NCC_ID);
        assertTrue(isDeleted);
        assertEquals(0, nhaCungCapDAO.findById(TEST_NCC_ID).getTrangThai());
        assertNull(nhaCungCapDAO.findByIdActive(TEST_NCC_ID), "Tìm active phải trả về null");

        boolean isRestored = nhaCungCapDAO.restore(TEST_NCC_ID);
        assertTrue(isRestored);
        assertEquals(1, nhaCungCapDAO.findById(TEST_NCC_ID).getTrangThai());
        assertNotNull(nhaCungCapDAO.findByIdActive(TEST_NCC_ID));
    }

    @Test
    @DisplayName("Tìm kiếm Nhà cung cấp theo từ khóa")
    void testSearch() {
        nhaCungCapDAO.insert(new NhaCungCapDTO(TEST_NCC_ID, "NCC Đặc Biệt", "0888888888", "Địa chỉ", 1));

        List<NhaCungCapDTO> results = nhaCungCapDAO.search("Đặc Biệt");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(n -> n.getMaNCC().equals(TEST_NCC_ID)));
    }
}