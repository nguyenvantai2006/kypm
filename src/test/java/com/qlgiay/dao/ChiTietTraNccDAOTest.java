package com.qlgiay.dao;

import com.qlgiay.dto.ChiTietTraNccDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp ChiTietTraNccDAO (Tương tác CSDL thật)")
class ChiTietTraNccDAOTest {

    private ChiTietTraNccDAO chiTietTraNccDAO;
    private final String TEST_PT_ID = "PT_TEST_99";
    private final String TEST_PN_ID = "PN_TEST_99";
    private final String TEST_SP_ID = "SP_TEST_99";
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
        chiTietTraNccDAO = new ChiTietTraNccDAO();
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
            c.prepareStatement("DELETE FROM CHI_TIET_TRA_NCC WHERE MaPT = '" + TEST_PT_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM PHIEU_TRA_NCC WHERE MaPT = '" + TEST_PT_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM CHI_TIET_PHIEU_NHAP WHERE MaPN = '" + TEST_PN_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM PHIEU_NHAP WHERE MaPN = '" + TEST_PN_ID + "'").executeUpdate();
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
            c.prepareStatement("INSERT INTO PHIEU_NHAP (MaPN, MaNV, MaNCC) VALUES ('" + TEST_PN_ID + "', '" + TEST_NV_ID
                    + "', '" + TEST_NCC_ID + "')").executeUpdate();
            c.prepareStatement("INSERT INTO PHIEU_TRA_NCC (MaPT, MaPN, MaNV, MaNCC, NgayTao) VALUES ('" + TEST_PT_ID
                    + "', '" + TEST_PN_ID + "', '" + TEST_NV_ID + "', '" + TEST_NCC_ID + "', GETDATE())")
                    .executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm và Tìm kiếm Chi tiết Phiếu Trả NCC")
    void testInsertAndFind() {
        ChiTietTraNccDTO ct = new ChiTietTraNccDTO(TEST_PT_ID, TEST_SP_ID, 10, new BigDecimal("500000"));

        try (Connection c = DBConnect.getConnection()) {
            boolean isInserted = chiTietTraNccDAO.insert(c, ct);
            assertTrue(isInserted, "Lệnh INSERT Chi tiết Trả NCC phải thành công");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        List<ChiTietTraNccDTO> list = chiTietTraNccDAO.findByMaPT(TEST_PT_ID);
        assertFalse(list.isEmpty());
        assertEquals(TEST_SP_ID, list.get(0).getMaSP());
        assertEquals(10, list.get(0).getSoLuong());
    }
}