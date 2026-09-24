package com.qlgiay.gui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PanelShadow extends JPanel {

    private static final Color CARD_BG = Color.WHITE;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 25);
    private static final Color ICON_BG = new Color(240, 247, 250);

    private final String iconPath;
    private final String title;
    private final String htmlDesc;

    public PanelShadow(String iconPath, String title, String htmlDesc) {
        this.iconPath = iconPath;
        this.title = title;
        this.htmlDesc = htmlDesc;
        initUI();
    }

    private void initUI() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 360));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(18, 18, 18, 18));
        add(inner, BorderLayout.CENTER);

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(true);
        iconWrap.setBackground(ICON_BG);
        iconWrap.setMaximumSize(new Dimension(260, 140));
        iconWrap.setPreferredSize(new Dimension(260, 140));
        iconWrap.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel();
        ImageIcon ic = loadAndScale(iconPath, 96, 96);
        if (ic != null) iconLabel.setIcon(ic);
        iconWrap.add(iconLabel);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel(htmlDesc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(70, 70, 70));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(iconWrap);
        inner.add(Box.createVerticalStrut(14));
        inner.add(lblTitle);
        inner.add(Box.createVerticalStrut(10));
        inner.add(lblDesc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 18;

        int shadowOffset = 6;
        Shape shadow = new RoundRectangle2D.Float(
                shadowOffset, shadowOffset,
                getWidth() - shadowOffset * 2,
                getHeight() - shadowOffset * 2,
                arc, arc
        );
        g2.setColor(SHADOW_COLOR);
        g2.fill(shadow);

        Shape card = new RoundRectangle2D.Float(
                0, 0,
                getWidth() - shadowOffset * 2,
                getHeight() - shadowOffset * 2,
                arc, arc
        );
        g2.setColor(CARD_BG);
        g2.translate(0, 0);
        g2.fill(card);

        g2.dispose();
        super.paintComponent(g);
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