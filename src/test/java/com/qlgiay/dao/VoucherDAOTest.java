package com.qlgiay.dao;

import com.qlgiay.dto.VoucherDTO;
import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp VoucherDAO (Tương tác CSDL thật)")
class VoucherDAOTest {

    private VoucherDAO voucherDAO;
    private final String TEST_VOUCHER_ID = "V_TEST_99";

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
        voucherDAO = new VoucherDAO();
        hardDeleteTestRecords();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecords();
    }

    private void hardDeleteTestRecords() {
        try (Connection c = DBConnect.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM VOUCHER WHERE MaVoucher = ?")) {
                ps.setString(1, TEST_VOUCHER_ID);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Thêm mới và Tìm kiếm Voucher")
    void testInsertAndFind() {
        VoucherDTO v = new VoucherDTO(TEST_VOUCHER_ID, "Voucher Test", 10, null, new BigDecimal("50000"),
                new BigDecimal("100000"), LocalDate.now(), LocalDate.now().plusDays(10), 100, 1);

        boolean isInserted = voucherDAO.insert(v);
        assertTrue(isInserted, "Lệnh INSERT Voucher phải thành công");

        VoucherDTO found = voucherDAO.findById(TEST_VOUCHER_ID);
        assertNotNull(found);
        assertEquals("Voucher Test", found.getTenVoucher());
        assertEquals(10, found.getPhanTramGiam());
    }

    @Test
    @DisplayName("Kiểm tra trừ số lượng Voucher (Khi khách hàng áp dụng)")
    void testDecreaseQuantity() {
        VoucherDTO v = new VoucherDTO(TEST_VOUCHER_ID, "Voucher Test", 10, null, new BigDecimal("50000"),
                new BigDecimal("100000"), LocalDate.now(), LocalDate.now().plusDays(10), 5, 1);
        voucherDAO.insert(v);

        try (Connection c = DBConnect.getConnection()) {
            boolean decreased = voucherDAO.decreaseQuantity(c, TEST_VOUCHER_ID, 2);
            assertTrue(decreased);

            VoucherDTO updated = voucherDAO.findById(TEST_VOUCHER_ID);
            assertEquals(3, updated.getSoLuong(), "Số lượng phải giảm từ 5 xuống 3");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Tìm kiếm Voucher hợp lệ để thanh toán (findValidById)")
    void testFindValidById() {
        // Tạo voucher yêu cầu đơn tối thiểu 100k
        VoucherDTO v = new VoucherDTO(TEST_VOUCHER_ID, "Voucher Test", 10, null, new BigDecimal("50000"),
                new BigDecimal("100000"), LocalDate.now(), LocalDate.now().plusDays(10), 5, 1);
        voucherDAO.insert(v);

        try (Connection c = DBConnect.getConnection()) {
            // Hóa đơn 150k -> Phải hợp lệ
            VoucherDTO valid = voucherDAO.findValidById(c, TEST_VOUCHER_ID, new BigDecimal("150000"));
            assertNotNull(valid, "Đơn hàng 150k phải đủ điều kiện áp dụng voucher >= 100k");

            // Hóa đơn 50k -> Phải bị từ chối (null)
            VoucherDTO invalid = voucherDAO.findValidById(c, TEST_VOUCHER_ID, new BigDecimal("50000"));
            assertNull(invalid, "Đơn hàng 50k không được phép áp dụng voucher >= 100k");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}