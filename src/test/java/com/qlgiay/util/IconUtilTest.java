package com.qlgiay.util;

import org.junit.jupiter.api.*;
import javax.swing.Icon;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UtilityTest")
@DisplayName("Kiểm thử lớp IconUtil (Xử lý Ảnh)")
class IconUtilTest {

    @BeforeEach
    void setUp() {
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Load icon với đường dẫn không tồn tại")
    void testLoadPngNotFound() {
        // Act: Gọi hàm load file ảnh với đường dẫn ảo
        Icon icon = IconUtil.loadPng("/icons/not_found_test_12345.png", 24);

        // Assert: Nếu không tìm thấy file, hàm phải xử lý an toàn và trả về null (không
        // crash app)
        assertNull(icon, "Đường dẫn sai phải được bắt lỗi và trả về null");
    }
}