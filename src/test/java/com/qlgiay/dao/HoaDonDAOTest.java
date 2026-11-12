package com.qlgiay.dao;

import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp HoaDonDAO (Tương tác CSDL thật)")
class HoaDonDAOTest {

    private HoaDonDAO hoaDonDAO;
    private final String TEST_HD_ID = "HD_TEST_99";
    private final String TEST_NV_ID = "NV_TEST_99";
    private final String TEST_Q_ID = "Q_TEST_99";

    @BeforeAll
    static void initAll() {
        try {
            DBConnect.enableTestConnection(); // Tái sử dụng kết nối
        } catch (Exception e) {
            fail("Không thể khởi tạo Test Connection: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        DBConnect.closeTestConnection();
    }

    @BeforeEach
    void setUp() {
        hoaDonDAO = new HoaDonDAO();
        hardDeleteTestRecords();
        insertDummyDependencies();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecords();
    }

    private void hardDeleteTestRecords() {
        try (Connection c = DBConnect.getConnection()) {
            // Xóa theo thứ tự: Con -> Cha -> Ông nội
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM HOA_DON WHERE MaHD = ?")) {
                ps.setString(1, TEST_HD_ID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM NHAN_VIEN WHERE MaNV = ?")) {
                ps.setString(1, TEST_NV_ID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM QUYEN WHERE MaQuyen = ?")) {
                ps.setString(1, TEST_Q_ID);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    private void insertDummyDependencies() {
        try (Connection c = DBConnect.getConnection()) {
            try (PreparedStatement ps = c
                    .prepareStatement("INSERT INTO QUYEN (MaQuyen, TenQuyen) VALUES (?, 'Quyen Test')")) {
                ps.setString(1, TEST_Q_ID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO NHAN_VIEN (MaNV, Ho, Ten, MaQuyen, TaiKhoan, MatKhau, Luong, TrangThai) VALUES (?, 'NV', 'Test', ?, 'test_nv', '123', 0, 1)")) {
                ps.setString(1, TEST_NV_ID);
                ps.setString(2, TEST_Q_ID);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Hóa đơn")
    void testInsertAndFind() {
        HoaDonDTO hd = new HoaDonDTO(TEST_HD_ID, TEST_NV_ID, null, null, LocalDate.now(), new BigDecimal("150000"));

        boolean isInserted = hoaDonDAO.insert(hd);
        assertTrue(isInserted, "Lệnh INSERT Hóa đơn phải thành công");

        HoaDonDTO found = hoaDonDAO.findById(TEST_HD_ID);
        assertNotNull(found);
        assertEquals(TEST_NV_ID, found.getMaNV());
        assertEquals(new BigDecimal("150000.00"), found.getTongTien()); // SQL Server decimal(18,2)
    }

    @Test
    @DisplayName("Cập nhật Tổng tiền Hóa đơn (Khi có chi tiết HD mới)")
    void testUpdateTongTien() {
        HoaDonDTO hd = new HoaDonDTO(TEST_HD_ID, TEST_NV_ID, null, null, LocalDate.now(), BigDecimal.ZERO);
        hoaDonDAO.insert(hd);

        try (Connection c = DBConnect.getConnection()) {
            boolean isUpdated = hoaDonDAO.updateTongTien(c, TEST_HD_ID, new BigDecimal("500000"));
            assertTrue(isUpdated);

            HoaDonDTO updated = hoaDonDAO.findById(TEST_HD_ID);
            assertEquals(new BigDecimal("500000.00"), updated.getTongTien());
        } catch (Exception e) {
            fail("Lỗi kết nối CSDL: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Tìm kiếm Hóa đơn theo từ khóa")
    void testSearch() {
        HoaDonDTO hd = new HoaDonDTO(TEST_HD_ID, TEST_NV_ID, null, null, LocalDate.now(), BigDecimal.ZERO);
        hoaDonDAO.insert(hd);

        List<HoaDonDTO> results = hoaDonDAO.search(TEST_HD_ID);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(h -> h.getMaHD().equals(TEST_HD_ID)));
    }
}