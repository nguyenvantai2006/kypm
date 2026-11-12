package com.qlgiay.util;

import org.junit.jupiter.api.*;
import javax.swing.JScrollPane;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UtilityTest")
@DisplayName("Kiểm thử lớp ScrollUtil (Cuộn mượt)")
class ScrollUtilTest {

    @BeforeEach
    void setUp() {
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Áp dụng Smooth Scroll cho JScrollPane")
    void testApplySmoothScroll() {
        // Arrange: Tạo một khung cuộn mặc định
        JScrollPane sp = new JScrollPane();
        assertTrue(sp.isWheelScrollingEnabled(), "Mặc định hệ thống phải bật cuộn tiêu chuẩn");

        // Act: Áp dụng tiện ích cuộn mượt
        ScrollUtil.applySmoothScroll(sp);

        // Assert:
        // 1. Phải tắt cuộn tiêu chuẩn của Java
        assertFalse(sp.isWheelScrollingEnabled(), "Sau khi apply, wheel scrolling mặc định phải bị tắt");

        // 2. Phải gắn thêm Custom MouseWheelListener của dự án
        assertTrue(sp.getMouseWheelListeners().length > 0, "Phải có MouseWheelListener tùy chỉnh được thêm vào");

        // 3. Tốc độ cuộn (Unit Increment) phải được set cứng là 16
        assertEquals(16, sp.getVerticalScrollBar().getUnitIncrement(),
                "Unit increment của thanh cuộn dọc phải được cấu hình là 16");
    }
}