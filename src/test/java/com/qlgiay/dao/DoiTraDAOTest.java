package com.qlgiay.dao;

import com.qlgiay.dto.DoiTraDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp DoiTraDAO (Tương tác CSDL thật)")
class DoiTraDAOTest {

    private DoiTraDAO doiTraDAO;
    private final String TEST_DT_ID = "DT_TEST_99";
    private final String TEST_HD_ID = "HD_TEST_99";
    private final String TEST_SP_ID = "SP_TEST_99";
    private final String TEST_NV_ID = "NV_TEST_99";
    private final String TEST_Q_ID = "Q_TEST_99";
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
        doiTraDAO = new DoiTraDAO();
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
            c.prepareStatement("DELETE FROM DOI_TRA WHERE MaDT = '" + TEST_DT_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM HOA_DON WHERE MaHD = '" + TEST_HD_ID + "'").executeUpdate();
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
            c.prepareStatement("INSERT INTO HOA_DON (MaHD, MaNV) VALUES ('" + TEST_HD_ID + "', '" + TEST_NV_ID + "')")
                    .executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Phiếu Đổi Trả")
    void testInsertAndFind() {
        DoiTraDTO dt = new DoiTraDTO(TEST_DT_ID, TEST_HD_ID, TEST_NV_ID, TEST_SP_ID, LocalDate.now(), 1,
                new BigDecimal("500000"), "Lỗi", "Đã hoàn tiền");

        boolean isInserted = doiTraDAO.insert(dt);
        assertTrue(isInserted, "Lệnh INSERT Đổi Trả phải thành công");

        DoiTraDTO found = doiTraDAO.findById(TEST_DT_ID);
        assertNotNull(found);
        assertEquals(TEST_HD_ID, found.getMaHD());
        assertEquals(TEST_SP_ID, found.getMaSP());
        assertEquals("Đã hoàn tiền", found.getTinhTrang());
    }

    @Test
    @DisplayName("Tìm kiếm Đổi Trả theo Mã Hóa Đơn")
    void testFindByMaHD() {
        doiTraDAO.insert(new DoiTraDTO(TEST_DT_ID, TEST_HD_ID, TEST_NV_ID, TEST_SP_ID, LocalDate.now(), 1,
                BigDecimal.ZERO, "Lỗi", "Hoàn tất"));

        List<DoiTraDTO> list = doiTraDAO.findByMaHD(TEST_HD_ID);
        assertFalse(list.isEmpty());
        assertEquals(TEST_DT_ID, list.get(0).getMaDT());
    }
}