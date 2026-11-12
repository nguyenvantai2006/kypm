package com.qlgiay.dao;

import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp ChiTietPhieuNhapDAO (Tương tác CSDL thật)")
class ChiTietPhieuNhapDAOTest {

    private ChiTietPhieuNhapDAO chiTietPhieuNhapDAO;
    private final String TEST_PN_ID = "PN_TEST_99";
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
        chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
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
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm và Tìm kiếm Chi tiết Phiếu Nhập")
    void testInsertAndFind() {
        ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO(TEST_PN_ID, TEST_SP_ID, 50, new BigDecimal("800000"));

        boolean isInserted = chiTietPhieuNhapDAO.insert(ct);
        assertTrue(isInserted, "Lệnh INSERT Chi tiết PN phải thành công");

        List<ChiTietPhieuNhapDTO> list = chiTietPhieuNhapDAO.findByMaPN(TEST_PN_ID);
        assertFalse(list.isEmpty());
        assertEquals(TEST_SP_ID, list.get(0).getMaSP());
        assertEquals(50, list.get(0).getSoLuong());
    }
}