package com.qlgiay.gui.component;

import com.qlgiay.dto.SanPhamDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ProductGridPanel extends JPanel {

    private Consumer<SanPhamDTO> productClickListener;
    private JPanel gridPanel;

    public ProductGridPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(4, 4, 4, 4));

        gridPanel = new JPanel(new GridLayout(0, 4, 12, 12));
        gridPanel.setOpaque(false);

        add(gridPanel, BorderLayout.NORTH);
    }

    public void setProductClickListener(Consumer<SanPhamDTO> listener) {
        this.productClickListener = listener;
    }

    public void loadProducts(List<SanPhamDTO> list) {
        gridPanel.removeAll();
        if (list != null) {
            for (SanPhamDTO sp : list) {
                ProductCard card = new ProductCard(sp);
                card.setClickListener(productClickListener);
                gridPanel.add(card);
            }
        }
        revalidate();
        repaint();
    }
}