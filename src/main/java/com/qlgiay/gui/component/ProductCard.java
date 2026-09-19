package com.qlgiay.gui.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.IconUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ProductCard extends JPanel {

    private final SanPhamDTO sanPham;
    private Consumer<SanPhamDTO> clickListener;

    public ProductCard(SanPhamDTO sp) {
        this.sanPham = sp;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(160, 230));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "background:#FFFFFF;" +
                        "border:1,1,1,1,#DCDCDC");

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);

        if (sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
            lblImage.setIcon(IconUtil.loadPng("/images/products/" + sp.getHinhAnh(), 110));
        }

        JLabel lblStock = new JLabel(" Kho: " + sp.getSoLuong() + " ");
        lblStock.setOpaque(true);
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStock.setBackground(new Color(230, 244, 234));
        lblStock.setForeground(new Color(46, 125, 50));
        lblStock.putClientProperty(FlatClientProperties.STYLE, "arc:10");

        JPanel stockWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        stockWrap.setOpaque(false);
        stockWrap.add(lblStock);

        imagePanel.add(stockWrap, BorderLayout.NORTH);
        imagePanel.add(lblImage, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(8, 4, 2, 4));

        JLabel lblTen = new JLabel("<html><body style='width:130px'>" + sp.getTenSP() + "</body></html>");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblSize = new JLabel("Size " + sp.getSize());
        lblSize.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSize.setForeground(new Color(100, 100, 100));

        JLabel lblGia = new JLabel(formatPrice(sp.getDonGia()));
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGia.setForeground(new Color(198, 40, 40));

        info.add(lblTen);
        info.add(lblSize);
        info.add(Box.createVerticalStrut(4));
        info.add(lblGia);

        add(imagePanel, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(245, 247, 250));
                putClientProperty(FlatClientProperties.STYLE,
                        "arc:20;" +
                                "background:#F5F7FA;" +
                                "border:1,1,1,1,#DCDCDC");
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.WHITE);
                putClientProperty(FlatClientProperties.STYLE,
                        "arc:20;" +
                                "background:#FFFFFF;" +
                                "border:1,1,1,1,#DCDCDC");
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickListener != null) {
                    clickListener.accept(sanPham);
                }
            }
        });
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "0đ";
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(price) + "đ";
    }

    public void setClickListener(Consumer<SanPhamDTO> listener) {
        this.clickListener = listener;
    }
}