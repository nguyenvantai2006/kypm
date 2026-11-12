package com.qlgiay.dao;

import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp NhanVienDAO (Tương tác CSDL thật)")
class NhanVienDAOTest {

    private NhanVienDAO nhanVienDAO;
    private final String TEST_NV_ID = "NV_TEST_99";
    private final String TEST_Q_ID = "Q_TEST_99";
    private final String TEST_TAI_KHOAN = "test_user_99";

    @BeforeEach
    void setUp() {
        nhanVienDAO = new NhanVienDAO();
        hardDeleteTestRecords();
        insertDummyQuyen(); // Khởi tạo Quyền giả để né lỗi Khóa ngoại
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecords();
    }

    private void hardDeleteTestRecords() {
        try (Connection c = DBConnect.getConnection()) {
            // Phải xóa Nhân viên (con) trước, Quyền (cha) sau
            try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM NHAN_VIEN WHERE MaNV = ?")) {
                ps1.setString(1, TEST_NV_ID);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement("DELETE FROM QUYEN WHERE MaQuyen = ?")) {
                ps2.setString(1, TEST_Q_ID);
                ps2.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    private void insertDummyQuyen() {
        try (Connection c = DBConnect.getConnection();
                PreparedStatement ps = c
                        .prepareStatement("INSERT INTO QUYEN (MaQuyen, TenQuyen) VALUES (?, 'Quyen Test')")) {
            ps.setString(1, TEST_Q_ID);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Nhân viên theo ID/Tài khoản")
    void testInsertAndFind() {
        NhanVienDTO nv = new NhanVienDTO(TEST_NV_ID, "Nguyễn", "Test", TEST_Q_ID, TEST_TAI_KHOAN, "123456",
                new BigDecimal("10000000"), 1);

        boolean isInserted = nhanVienDAO.insert(nv);
        assertTrue(isInserted, "Lệnh INSERT nhân viên phải thành công");

        NhanVienDTO byId = nhanVienDAO.findById(TEST_NV_ID);
        assertNotNull(byId, "Tìm theo ID phải ra kết quả");
        assertEquals("Nguyễn", byId.getHo());

        NhanVienDTO byTk = nhanVienDAO.findByTaiKhoan(TEST_TAI_KHOAN);
        assertNotNull(byTk, "Tìm theo Tài khoản phải ra kết quả");
        assertEquals(TEST_NV_ID, byTk.getMaNV());
    }

    @Test
    @DisplayName("Cập nhật thông tin và đổi Mật khẩu")
    void testUpdate() {
        // Arrange
        NhanVienDTO nv = new NhanVienDTO(TEST_NV_ID, "Họ Cũ", "Tên Cũ", TEST_Q_ID, TEST_TAI_KHOAN, "passcu",
                new BigDecimal("5000000"), 1);
        nhanVienDAO.insert(nv);

        // Act
        nv.setHo("Họ Mới");
        nv.setMatKhau("passmoi"); // Đổi pass
        nv.setLuong(new BigDecimal("15000000"));
        boolean isUpdated = nhanVienDAO.update(nv);

        // Assert
        NhanVienDTO updatedNV = nhanVienDAO.findById(TEST_NV_ID);
        assertTrue(isUpdated);
        assertEquals("Họ Mới", updatedNV.getHo());
        assertEquals("passmoi", updatedNV.getMatKhau());
        assertEquals(new BigDecimal("15000000.00"), updatedNV.getLuong()); // SQL Server Decimal thường trả về có .00
    }

    @Test
    @DisplayName("Cập nhật Trạng thái (Khóa/Mở khóa)")
    void testUpdateTrangThai() {
        nhanVienDAO
                .insert(new NhanVienDTO(TEST_NV_ID, "Họ", "Tên", TEST_Q_ID, TEST_TAI_KHOAN, "123", BigDecimal.ZERO, 1));

        boolean locked = nhanVienDAO.updateTrangThai(TEST_NV_ID, 0);
        assertTrue(locked);
        assertEquals(0, nhanVienDAO.findById(TEST_NV_ID).getTrangThai());

        boolean unlocked = nhanVienDAO.updateTrangThai(TEST_NV_ID, 1);
        assertTrue(unlocked);
        assertEquals(1, nhanVienDAO.findById(TEST_NV_ID).getTrangThai());
    }

    @Test
    @DisplayName("Tìm kiếm Nhân viên theo Từ khóa (Search)")
    void testSearch() {
        nhanVienDAO.insert(
                new NhanVienDTO(TEST_NV_ID, "Đoàn", "Dự", TEST_Q_ID, TEST_TAI_KHOAN, "123", BigDecimal.ZERO, 1));

        List<NhanVienDTO> results = nhanVienDAO.search("Đoàn");
        assertFalse(results.isEmpty(), "Phải tìm thấy nhân viên vừa thêm");

        boolean found = results.stream().anyMatch(n -> n.getMaNV().equals(TEST_NV_ID));
        assertTrue(found, "Kết quả tìm kiếm phải chứa TEST_NV_ID");
    }
}