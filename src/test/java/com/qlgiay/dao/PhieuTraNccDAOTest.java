package com.qlgiay.dao;

import com.qlgiay.dto.PhieuTraNccDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp PhieuTraNccDAO (Tương tác CSDL thật)")
class PhieuTraNccDAOTest {

    private PhieuTraNccDAO phieuTraNccDAO;
    private final String TEST_PT_ID = "PT_TEST_99";
    private final String TEST_PN_ID = "PN_TEST_99";
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
        phieuTraNccDAO = new PhieuTraNccDAO();
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
            c.prepareStatement("DELETE FROM PHIEU_TRA_NCC WHERE MaPT = '" + TEST_PT_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM PHIEU_NHAP WHERE MaPN = '" + TEST_PN_ID + "'").executeUpdate();
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
            c.prepareStatement("INSERT INTO PHIEU_NHAP (MaPN, MaNV, MaNCC) VALUES ('" + TEST_PN_ID + "', '" + TEST_NV_ID
                    + "', '" + TEST_NCC_ID + "')").executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Phiếu Trả Nhà Cung Cấp")
    void testInsertAndFind() {
        PhieuTraNccDTO pt = new PhieuTraNccDTO();
        pt.setMaPT(TEST_PT_ID);
        pt.setMaPN(TEST_PN_ID);
        pt.setMaNV(TEST_NV_ID);
        pt.setMaNCC(TEST_NCC_ID);
        pt.setNgayTao(LocalDate.now());
        pt.setTongSoMatHang(10);
        pt.setTongTien(new BigDecimal("1000000"));
        pt.setLyDo("Hàng cận date");
        pt.setTrangThai("Đang duyệt");

        try (Connection c = DBConnect.getConnection()) {
            boolean isInserted = phieuTraNccDAO.insert(c, pt);
            assertTrue(isInserted, "Lệnh INSERT Phiếu Trả NCC phải thành công");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        PhieuTraNccDTO found = phieuTraNccDAO.findById(TEST_PT_ID);
        assertNotNull(found);
        assertEquals(TEST_PN_ID, found.getMaPN());
        assertEquals("Hàng cận date", found.getLyDo());
    }

    @Test
    @DisplayName("Cập nhật trạng thái duyệt Phiếu Trả NCC")
    void testUpdateStatus() {
        PhieuTraNccDTO pt = new PhieuTraNccDTO();
        pt.setMaPT(TEST_PT_ID);
        pt.setMaPN(TEST_PN_ID);
        pt.setMaNV(TEST_NV_ID);
        pt.setMaNCC(TEST_NCC_ID);
        pt.setNgayTao(LocalDate.now());
        pt.setTrangThai("Đang duyệt");

        try (Connection c = DBConnect.getConnection()) {
            phieuTraNccDAO.insert(c, pt);

            boolean isUpdated = phieuTraNccDAO.updateStatus(c, TEST_PT_ID, "Đã duyệt", TEST_NV_ID);
            assertTrue(isUpdated);

            PhieuTraNccDTO updated = phieuTraNccDAO.findById(TEST_PT_ID);
            assertEquals("Đã duyệt", updated.getTrangThai());
            assertEquals(TEST_NV_ID, updated.getNguoiXuLy());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}