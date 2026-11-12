package com.qlgiay.dao;

import com.qlgiay.dto.PhieuNhapDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp PhieuNhapDAO (Tương tác CSDL thật)")
class PhieuNhapDAOTest {

    private PhieuNhapDAO phieuNhapDAO;
    private final String TEST_PN_ID = "PN_TEST_99";
    private final String TEST_NV_ID = "NV_TEST_99";
    private final String TEST_Q_ID = "Q_TEST_99";
    private final String TEST_NCC_ID = "NCC_TEST_99";

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
        phieuNhapDAO = new PhieuNhapDAO();
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
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM PHIEU_NHAP WHERE MaPN = ?")) {
                ps.setString(1, TEST_PN_ID);
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
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM NHA_CUNG_CAP WHERE MaNCC = ?")) {
                ps.setString(1, TEST_NCC_ID);
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
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO NHA_CUNG_CAP (MaNCC, TenNCC, TrangThai) VALUES (?, 'NCC Test', 1)")) {
                ps.setString(1, TEST_NCC_ID);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Phiếu Nhập")
    void testInsertAndFind() {
        PhieuNhapDTO pn = new PhieuNhapDTO(TEST_PN_ID, TEST_NV_ID, TEST_NCC_ID, LocalDate.now(), 50,
                new BigDecimal("10000000"));

        boolean isInserted = phieuNhapDAO.insert(pn);
        assertTrue(isInserted, "Lệnh INSERT Phiếu Nhập phải thành công");

        PhieuNhapDTO found = phieuNhapDAO.findById(TEST_PN_ID);
        assertNotNull(found);
        assertEquals(TEST_NV_ID, found.getMaNV());
        assertEquals(TEST_NCC_ID, found.getMaNCC());
        assertEquals(50, found.getTongSoMatHang());
    }

    @Test
    @DisplayName("Cập nhật Tổng tiền Phiếu Nhập (Khi thêm chi tiết)")
    void testUpdateTongTien() {
        PhieuNhapDTO pn = new PhieuNhapDTO(TEST_PN_ID, TEST_NV_ID, TEST_NCC_ID, LocalDate.now(), 0, BigDecimal.ZERO);
        phieuNhapDAO.insert(pn);

        try (Connection c = DBConnect.getConnection()) {
            boolean isUpdated = phieuNhapDAO.updateTongTien(c, TEST_PN_ID, new BigDecimal("2000000"));
            assertTrue(isUpdated);

            PhieuNhapDTO updated = phieuNhapDAO.findById(TEST_PN_ID);
            assertEquals(new BigDecimal("2000000.00"), updated.getTongTien());
        } catch (Exception e) {
            fail("Lỗi kết nối CSDL: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Tìm kiếm Phiếu Nhập theo từ khóa")
    void testSearch() {
        PhieuNhapDTO pn = new PhieuNhapDTO(TEST_PN_ID, TEST_NV_ID, TEST_NCC_ID, LocalDate.now(), 10,
                new BigDecimal("500000"));
        phieuNhapDAO.insert(pn);

        // Tìm theo mã Nhà cung cấp test
        List<PhieuNhapDTO> results = phieuNhapDAO.search(TEST_NCC_ID);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getMaPN().equals(TEST_PN_ID)));
    }
}