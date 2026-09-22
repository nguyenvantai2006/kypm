package com.qlgiay.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.IconUtil;

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

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pricePanel.setOpaque(false);

        JLabel lblGia = new JLabel();
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGia.setForeground(new Color(198, 40, 40));

        if (sp.getGiaKhuyenMai() != null && sp.getGiaKhuyenMai().compareTo(BigDecimal.ZERO) > 0
                && sp.getGiaKhuyenMai().compareTo(sp.getDonGia() == null ? BigDecimal.ZERO : sp.getDonGia()) < 0) {
            JLabel lblGiaCu = new JLabel("<html><s>" + formatPrice(sp.getDonGia()) + "</s></html>");
            lblGiaCu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblGiaCu.setForeground(new Color(120, 120, 120));

            lblGia.setText(formatPrice(sp.getGiaKhuyenMai()));
            pricePanel.add(lblGiaCu);
            pricePanel.add(lblGia);
        } else {
            lblGia.setText(formatPrice(sp.getDonGia()));
            pricePanel.add(lblGia);
        }

        info.add(lblTen);
        info.add(lblSize);
        info.add(Box.createVerticalStrut(4));
        info.add(pricePanel);

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