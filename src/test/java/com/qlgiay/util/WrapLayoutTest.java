package com.qlgiay.util;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UtilityTest")
@DisplayName("Kiểm thử lớp WrapLayout (Bố cục rớt dòng)")
class WrapLayoutTest {

    @BeforeEach
    void setUp() {
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Kiểm tra tính toán kích thước (Preferred Layout Size) khi bị thu hẹp ngang")
    void testPreferredLayoutSize() {
        // Arrange: Khởi tạo JPanel với WrapLayout
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));

        // Thêm 3 component có kích thước 100x50 vào panel
        for (int i = 0; i < 3; i++) {
            JButton btn = new JButton("Test " + i);
            btn.setPreferredSize(new Dimension(100, 50));
            panel.add(btn);
        }

        // Kịch bản 1: Giả lập Container đủ rộng để chứa cả 3 component trên 1 hàng
        // (Width = 500)
        panel.setSize(500, 100);
        Dimension prefSizeWide = panel.getLayout().preferredLayoutSize(panel);
        assertTrue(prefSizeWide.width > 0, "Width phải được tính toán hợp lệ");

        // Kịch bản 2: Giả lập Container hẹp, phải rớt xuống các hàng dưới (Width = 150)
        // Với chiều rộng 150, mỗi nút 100px sẽ chiếm 1 hàng độc lập
        panel.setSize(150, 100);
        Dimension prefSizeNarrow = panel.getLayout().preferredLayoutSize(panel);

        // Assert: Chiều cao yêu cầu của khung hẹp phải LỚN HƠN chiều cao của khung rộng
        // (vì rớt dòng)
        assertTrue(prefSizeNarrow.height > prefSizeWide.height,
                "Khi bị thu hẹp ngang, layout phải tính toán tăng chiều cao (height) để wrap các component xuống dòng");
    }
}