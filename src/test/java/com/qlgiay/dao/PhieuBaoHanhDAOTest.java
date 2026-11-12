package com.qlgiay.dao;

import com.qlgiay.dto.PhieuBaoHanhDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp PhieuBaoHanhDAO (Tương tác CSDL thật)")
class PhieuBaoHanhDAOTest {

    private PhieuBaoHanhDAO phieuBaoHanhDAO;
    private final String TEST_PBH_ID = "BH_TEST_99";
    private final String TEST_HD_ID = "HD_TEST_99";
    private final String TEST_SP_ID = "SP_TEST_99";
    private final String TEST_KH_ID = "KH_TEST_99";
    private final String TEST_NV_ID = "NV_TEST_99";
    private final String TEST_Q_ID = "Q_TEST_99";
    private final String TEST_NCC_ID = "NCC_TEST_99";

    @BeforeAll
    static void initAll() {
        try {
            DBConnect.enableTestConnection();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        DBConnect.closeTestConnection();
    }

    @BeforeEach
    void setUp() {
        phieuBaoHanhDAO = new PhieuBaoHanhDAO();
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
            c.prepareStatement("DELETE FROM PHIEU_BAO_HANH WHERE MaPBH = '" + TEST_PBH_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM HOA_DON WHERE MaHD = '" + TEST_HD_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM KHACH_HANG WHERE MaKH = '" + TEST_KH_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM SAN_PHAM WHERE MaSP = '" + TEST_SP_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM NHA_CUNG_CAP WHERE MaNCC = '" + TEST_NCC_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM NHAN_VIEN WHERE MaNV = '" + TEST_NV_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM QUYEN WHERE MaQuyen = '" + TEST_Q_ID + "'").executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private void insertDummyDependencies() {
        try (Connection c = DBConnect.getConnection()) {
            c.prepareStatement("INSERT INTO QUYEN (MaQuyen, TenQuyen) VALUES ('" + TEST_Q_ID + "', 'Quyen Test')")
                    .executeUpdate();
            c.prepareStatement("INSERT INTO NHAN_VIEN (MaNV, MaQuyen, TaiKhoan, MatKhau) VALUES ('" + TEST_NV_ID
                    + "', '" + TEST_Q_ID + "', 'tk_test', '123')").executeUpdate();
            c.prepareStatement("INSERT INTO NHA_CUNG_CAP (MaNCC, TenNCC) VALUES ('" + TEST_NCC_ID + "', 'NCC Test')")
                    .executeUpdate();
            c.prepareStatement("INSERT INTO SAN_PHAM (MaSP, TenSP, MaNCC) VALUES ('" + TEST_SP_ID + "', 'SP Test', '"
                    + TEST_NCC_ID + "')").executeUpdate();
            c.prepareStatement(
                    "INSERT INTO KHACH_HANG (MaKH, TenKH, TrangThai) VALUES ('" + TEST_KH_ID + "', 'KH Test', 1)")
                    .executeUpdate();
            c.prepareStatement("INSERT INTO HOA_DON (MaHD, MaNV, MaKH) VALUES ('" + TEST_HD_ID + "', '" + TEST_NV_ID
                    + "', '" + TEST_KH_ID + "')").executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Phiếu Bảo Hành")
    void testInsertAndFind() {
        PhieuBaoHanhDTO bh = new PhieuBaoHanhDTO(TEST_PBH_ID, TEST_HD_ID, TEST_SP_ID, TEST_KH_ID, LocalDate.now(),
                LocalDate.now().plusDays(7), "Bung keo", BigDecimal.ZERO, 0);

        boolean isInserted = phieuBaoHanhDAO.insert(bh);
        assertTrue(isInserted, "Lệnh INSERT Bảo Hành phải thành công");

        PhieuBaoHanhDTO found = phieuBaoHanhDAO.findById(TEST_PBH_ID);
        assertNotNull(found);
        assertEquals(TEST_KH_ID, found.getMaKH());
        assertEquals("Bung keo", found.getLoiCanBaoHanh());
    }

    @Test
    @DisplayName("Cập nhật trạng thái Phiếu Bảo Hành")
    void testUpdateTrangThai() {
        phieuBaoHanhDAO.insert(new PhieuBaoHanhDTO(TEST_PBH_ID, TEST_HD_ID, TEST_SP_ID, TEST_KH_ID, LocalDate.now(),
                LocalDate.now(), "Lỗi", BigDecimal.ZERO, 0));

        boolean isUpdated = phieuBaoHanhDAO.updateTrangThai(TEST_PBH_ID, 1); // 1: Đã xong
        assertTrue(isUpdated);

        assertEquals(1, phieuBaoHanhDAO.findById(TEST_PBH_ID).getTrangThai());
    }
}