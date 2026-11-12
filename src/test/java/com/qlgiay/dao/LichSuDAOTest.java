package com.qlgiay.dao;

import com.qlgiay.dto.LichSuDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp LichSuDAO (Tương tác CSDL thật)")
class LichSuDAOTest {

    private LichSuDAO lichSuDAO;
    private final String TEST_PHAN_LOAI = "TEST_CATEGORY_99";
    private final String TEST_NV_ID = "NV_TEST_99";

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
        lichSuDAO = new LichSuDAO();
        hardDeleteTestRecords();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecords();
    }

    private void hardDeleteTestRecords() {
        try (Connection c = DBConnect.getConnection()) {
            // Xóa dựa trên PhanLoai để không ảnh hưởng đến log thật của hệ thống
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM LICH_SU_HOAT_DONG WHERE PhanLoai = ?")) {
                ps.setString(1, TEST_PHAN_LOAI);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Lịch sử hoạt động")
    void testInsertAndFind() {
        LichSuDTO log = new LichSuDTO();
        log.setMaNV(TEST_NV_ID);
        log.setPhanLoai(TEST_PHAN_LOAI);
        log.setHanhDong("Tạo hóa đơn Test");
        log.setDoiTuong("Hóa đơn HD_TEST_99");
        log.setThoiGian(LocalDateTime.now());
        log.setChiTiet("Đã thanh toán 500k");

        // Act: Insert
        boolean isInserted = lichSuDAO.insert(log);
        assertTrue(isInserted, "Lệnh INSERT Lịch sử phải thành công");

        // Act: Find (Nới lỏng khoảng thời gian LocalDate từ hôm qua đến ngày mai để
        // tránh lệch giờ với GETDATE)
        LocalDate today = LocalDate.now();
        List<LichSuDTO> results = lichSuDAO.findByPhanLoaiAndDate(TEST_PHAN_LOAI, today.minusDays(1),
                today.plusDays(1));

        // Assert
        assertFalse(results.isEmpty(), "Phải tìm thấy lịch sử vừa thêm");
        assertEquals(TEST_NV_ID, results.get(0).getMaNV());
        assertEquals("Tạo hóa đơn Test", results.get(0).getHanhDong());
        assertEquals("Hóa đơn HD_TEST_99", results.get(0).getDoiTuong());
    }
}