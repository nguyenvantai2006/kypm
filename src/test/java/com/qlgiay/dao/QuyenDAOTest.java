package com.qlgiay.dao;

import com.qlgiay.dto.QuyenDTO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp QuyenDAO (Tương tác CSDL thật)")
class QuyenDAOTest {

    private QuyenDAO quyenDAO;
    private final String TEST_ID = "Q_TEST_99";

    @BeforeEach
    void setUp() {
        quyenDAO = new QuyenDAO();
        // Dọn dẹp trước nếu có rác từ lần test bị crash trước đó
        quyenDAO.delete(TEST_ID);
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        // Dọn dẹp sau khi test xong để trả lại DB sạch sẽ
        quyenDAO.delete(TEST_ID);
    }

    @Test
    @DisplayName("Thêm mới Quyền (Insert) và Tìm kiếm (FindById)")
    void testInsertAndFindById() {
        // Arrange
        QuyenDTO q = new QuyenDTO(TEST_ID, "Quyền Test", 1, 0, 1, 0, 1, 0, 0, 0, 0);

        // Act
        boolean isInserted = quyenDAO.insert(q);
        QuyenDTO found = quyenDAO.findById(TEST_ID);

        // Assert
        assertTrue(isInserted, "Lệnh INSERT phải trả về true");
        assertNotNull(found, "Phải tìm thấy dòng dữ liệu vừa thêm");
        assertEquals("Quyền Test", found.getTenQuyen());
        assertEquals(1, found.getQlBanHang());
        assertEquals(0, found.getQlKhachHang());
        System.out.println("[QuyenDAOTest] Insert & FindById Pass");
    }

    @Test
    @DisplayName("Cập nhật Quyền (Update)")
    void testUpdate() {
        // Arrange: Insert trước
        QuyenDTO q = new QuyenDTO(TEST_ID, "Quyền Cũ", 0, 0, 0, 0, 0, 0, 0, 0, 0);
        quyenDAO.insert(q);

        // Act: Đổi thông tin và Update
        q.setTenQuyen("Quyền Mới");
        q.setQlBanHang(1);
        boolean isUpdated = quyenDAO.update(q);

        QuyenDTO found = quyenDAO.findById(TEST_ID);

        // Assert
        assertTrue(isUpdated, "Lệnh UPDATE phải trả về true");
        assertNotNull(found);
        assertEquals("Quyền Mới", found.getTenQuyen(), "Tên quyền phải được cập nhật");
        assertEquals(1, found.getQlBanHang(), "Cờ bán hàng phải được cập nhật thành 1");
        System.out.println("[QuyenDAOTest] Update Pass");
    }

    @Test
    @DisplayName("Xóa Quyền (Delete)")
    void testDelete() {
        // Arrange
        quyenDAO.insert(new QuyenDTO(TEST_ID, "Tạm thời", 0, 0, 0, 0, 0, 0, 0, 0, 0));

        // Act
        boolean isDeleted = quyenDAO.delete(TEST_ID);
        QuyenDTO found = quyenDAO.findById(TEST_ID);

        // Assert
        assertTrue(isDeleted, "Lệnh DELETE phải trả về true");
        assertNull(found, "Không được tìm thấy dữ liệu sau khi xóa");
        System.out.println("[QuyenDAOTest] Delete Pass");
    }
}