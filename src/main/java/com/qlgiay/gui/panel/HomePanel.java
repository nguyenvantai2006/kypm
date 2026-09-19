package com.qlgiay.gui.panel;

import com.qlgiay.dto.AuthSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.qlgiay.gui.component.PanelShadow;

public class HomePanel extends JPanel {

    private final AuthSession session;

    private static final Color MAIN_COLOR = Color.WHITE;
    private static final Color BG_COLOR = new Color(240, 247, 250);

    private static final Object[][] CARDS = {
            {"TÍNH CHÍNH XÁC", "/images/home/accuracy.png",
                    "<html>Mỗi sản phẩm là một mã duy nhất.<br>" +
                            "Giúp theo dõi thông tin rõ ràng,<br>" +
                            "đảm bảo dữ liệu chuẩn và tin cậy.</html>"},
            {"TÍNH BẢO MẬT", "/images/home/security.png",
                    "<html>Phân quyền theo vai trò nhân viên.<br>" +
                            "Ngăn thao tác trái phép và kiểm soát<br>" +
                            "hoạt động hệ thống tốt hơn.</html>"},
            {"TÍNH HIỆU QUẢ", "/images/home/efficiency.png",
                    "<html>Tra cứu nhanh, quản lý tập trung.<br>" +
                            "Hỗ trợ bán hàng - nhập hàng - báo cáo<br>" +
                            "mượt và nhất quán.</html>"}
    };

    public HomePanel(AuthSession session) {
        this.session = session;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(true);
        setBackground(BG_COLOR);
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MAIN_COLOR);
        top.setPreferredSize(new Dimension(0, 300));
        top.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel banner = new JLabel();
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon bannerIcon = loadAndScale("/images/home/banner.png", 880, 268);
        if (bannerIcon != null) banner.setIcon(bannerIcon);
        else banner.setText("<html><div style='text-align:center;'>Thiếu ảnh banner<br>/images/home/banner.png</div></html>");

        top.add(banner, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 35));
        center.setOpaque(true);
        center.setBackground(BG_COLOR);
        center.setBorder(new EmptyBorder(10, 10, 20, 10));

        for (Object[] c : CARDS) {
            String title = (String) c[0];
            String iconPath = (String) c[1];
            String html = (String) c[2];
            center.add(new PanelShadow(iconPath, title, html));
        }

        add(center, BorderLayout.CENTER);
    }

    private ImageIcon loadAndScale(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }
}