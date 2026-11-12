package com.qlgiay.dao;

import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp ChiTietHoaDonDAO (Tương tác CSDL thật)")
class ChiTietHoaDonDAOTest {

    private ChiTietHoaDonDAO chiTietHoaDonDAO;
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
        chiTietHoaDonDAO = new ChiTietHoaDonDAO();
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
            c.prepareStatement("DELETE FROM CHI_TIET_HOA_DON WHERE MaHD = '" + TEST_HD_ID + "'").executeUpdate();
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
    @DisplayName("Thêm và Tìm kiếm Chi tiết Hóa đơn")
    void testInsertAndFind() {
        ChiTietHoaDonDTO ct = new ChiTietHoaDonDTO(TEST_HD_ID, TEST_SP_ID, 2, new BigDecimal("150000"));

        boolean isInserted = chiTietHoaDonDAO.insert(ct);
        assertTrue(isInserted, "Lệnh INSERT Chi tiết HD phải thành công");

        List<ChiTietHoaDonDTO> list = chiTietHoaDonDAO.findByMaHD(TEST_HD_ID);
        assertFalse(list.isEmpty());
        assertEquals(TEST_SP_ID, list.get(0).getMaSP());
        assertEquals(2, list.get(0).getSoLuong());
    }
}