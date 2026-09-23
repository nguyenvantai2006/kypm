package com.qlgiay.gui.component;

import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ProductGridPanel extends JPanel implements Scrollable {

    private Consumer<SanPhamDTO> productClickListener;

    public ProductGridPanel() {
        setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(4, 4, 4, 4));
    }

    public void setProductClickListener(Consumer<SanPhamDTO> listener) {
        this.productClickListener = listener;
    }

    public void loadProducts(List<SanPhamDTO> list) {
        removeAll();
        if (list != null) {
            for (SanPhamDTO sp : list) {
                ProductCard card = new ProductCard(sp);
                card.setClickListener(productClickListener);
                add(card);
            }
        }
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(520, 420);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? visibleRect.height - 24 : visibleRect.width - 24;
    }
}