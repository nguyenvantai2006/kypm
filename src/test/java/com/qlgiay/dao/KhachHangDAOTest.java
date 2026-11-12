package com.qlgiay.dao;

import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp KhachHangDAO (Tương tác CSDL thật)")
class KhachHangDAOTest {

    private KhachHangDAO khachHangDAO;
    private final String TEST_KH_ID = "KH_TEST_99";
    private final String TEST_PHONE = "0999888777";

    // ==========================================
    // CÁC HÀM HỖ TRỢ IN OUTPUT RA CONSOLE
    // ==========================================
    private void assertBooleanResult(String testCase, String input, boolean expected, boolean actual) {
        System.out.printf("[KhachHangDAOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    private void assertNotNullResult(String testCase, String input, Object actual) {
        System.out.printf("[KhachHangDAOTest] %s | input=%s | expected=NotNull | actual=%s%n",
                testCase, input, actual != null ? "NotNull" : "null");
        assertNotNull(actual);
    }

    // ==========================================
    // QUẢN LÝ VÒNG ĐỜI BÀI TEST
    // ==========================================
    @BeforeAll
    static void initAll() {
        try {
            DBConnect.getConnection().close();
        } catch (Exception ignored) {
        }
    }

    @AfterAll
    static void tearDownAll() {
    }

    @BeforeEach
    void setUp() {
        khachHangDAO = new KhachHangDAO();
        hardDeleteTestRecord(); // Dọn dẹp trước khi test
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecord(); // Dọn dẹp sau khi test
    }

    // Xóa vật lý record test ra khỏi CSDL để không ảnh hưởng dữ liệu thật
    private void hardDeleteTestRecord() {
        try (Connection c = DBConnect.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM KHACH_HANG WHERE MaKH = ?")) {
                ps.setString(1, TEST_KH_ID);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    private KhachHangDTO createTestCustomer() {
        return new KhachHangDTO(TEST_KH_ID, "Khach Hang Test", TEST_PHONE, "Dia chi test", 100, 1);
    }

    // ==========================================
    // CÁC KỊCH BẢN KIỂM THỬ (TEST CASES)
    // ==========================================

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Khách Hàng")
    void testInsertAndFind() {
        KhachHangDTO kh = createTestCustomer();

        // Test Insert
        boolean isInserted = khachHangDAO.insert(kh);
        assertBooleanResult("Them khach hang moi", "MaKH=" + TEST_KH_ID, true, isInserted);

        // Test FindById
        KhachHangDTO foundById = khachHangDAO.findById(TEST_KH_ID);
        assertNotNullResult("Tim KH bang ID", "MaKH=" + TEST_KH_ID, foundById);
        assertEquals("Khach Hang Test", foundById.getTenKH());

        // Test FindByPhone
        KhachHangDTO foundByPhone = khachHangDAO.findByPhone(TEST_PHONE);
        assertNotNullResult("Tim KH bang SDT", "SDT=" + TEST_PHONE, foundByPhone);
    }

    @Test
    @DisplayName("Cập nhật thông tin Khách Hàng")
    void testUpdate() {
        khachHangDAO.insert(createTestCustomer());

        KhachHangDTO khUpdate = new KhachHangDTO(TEST_KH_ID, "Ten Moi", TEST_PHONE, "Dia chi moi", 200, 1);
        boolean isUpdated = khachHangDAO.update(khUpdate);
        assertBooleanResult("Cap nhat thong tin KH", "MaKH=" + TEST_KH_ID, true, isUpdated);

        KhachHangDTO updatedKH = khachHangDAO.findById(TEST_KH_ID);
        assertEquals("Ten Moi", updatedKH.getTenKH());
        assertEquals(200, updatedKH.getDiemTichLuy());
    }

    @Test
    @DisplayName("Xóa mềm và Khôi phục Khách Hàng")
    void testSoftDeleteAndRestore() {
        khachHangDAO.insert(createTestCustomer());

        // Xóa mềm
        boolean isDeleted = khachHangDAO.softDelete(TEST_KH_ID);
        assertBooleanResult("Xoa mem KH", "MaKH=" + TEST_KH_ID, true, isDeleted);
        assertEquals(0, khachHangDAO.findById(TEST_KH_ID).getTrangThai());
        assertNull(khachHangDAO.findByIdActive(TEST_KH_ID), "KH xoa mem khong duoc xuat hien trong findByIdActive");

        // Khôi phục
        boolean isRestored = khachHangDAO.restore(TEST_KH_ID);
        assertBooleanResult("Khoi phuc KH", "MaKH=" + TEST_KH_ID, true, isRestored);
        assertEquals(1, khachHangDAO.findById(TEST_KH_ID).getTrangThai());
    }

    @Test
    @DisplayName("Tìm kiếm Khách Hàng theo từ khóa")
    void testSearch() {
        khachHangDAO.insert(createTestCustomer());

        List<KhachHangDTO> results = khachHangDAO.search(TEST_PHONE);
        assertBooleanResult("Tim kiem KH theo tu khoa", "Keyword=" + TEST_PHONE, true, !results.isEmpty());
        assertTrue(results.stream().anyMatch(k -> k.getMaKH().equals(TEST_KH_ID)));
    }

    @Test
    @DisplayName("Cộng và Trừ điểm tích lũy Khách Hàng")
    void testPointsSystem() {
        khachHangDAO.insert(createTestCustomer()); // Khởi tạo với 100 điểm

        try (Connection c = DBConnect.getConnection()) {
            // Cộng 50 điểm
            boolean isAdded = khachHangDAO.addPoints(c, TEST_KH_ID, 50);
            assertBooleanResult("Cong 50 diem tich luy", "DiemCong=50", true, isAdded);
            assertEquals(150, khachHangDAO.getPoints(c, TEST_KH_ID));

            // Trừ 30 điểm
            boolean isUsed = khachHangDAO.usePoints(c, TEST_KH_ID, 30);
            assertBooleanResult("Tru 30 diem tich luy", "DiemTru=30", true, isUsed);
            assertEquals(120, khachHangDAO.getPoints(c, TEST_KH_ID));

            // Trừ vượt mức (200 điểm > 120 điểm hiện có) -> Phải thất bại
            boolean overUse = khachHangDAO.usePoints(c, TEST_KH_ID, 200);
            assertBooleanResult("Tru vuot qua so diem hien co", "DiemTru=200", false, overUse);

        } catch (Exception e) {
            fail("Lỗi kết nối CSDL trong lúc test điểm tích lũy: " + e.getMessage());
        }
    }
}