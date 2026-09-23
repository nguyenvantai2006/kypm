package com.qlgiay.util;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.Component;

public class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int alignment, int horizontalGap, int verticalGap) {
        super(alignment, horizontalGap, verticalGap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= getHgap() + 1;
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth <= 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            Insets insets = target.getInsets();
            int maxWidth = targetWidth - insets.left - insets.right - getHgap() * 2;
            int rowWidth = 0;
            int rowHeight = 0;
            int height = insets.top + insets.bottom;
            int visibleCount = 0;

            for (Component component : target.getComponents()) {
                if (!component.isVisible()) {
                    continue;
                }

                Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
                if (rowWidth > 0 && rowWidth + getHgap() + size.width > maxWidth) {
                    height += rowHeight + getVgap();
                    rowWidth = 0;
                    rowHeight = 0;
                }

                if (rowWidth > 0) {
                    rowWidth += getHgap();
                }
                rowWidth += size.width;
                rowHeight = Math.max(rowHeight, size.height);
                visibleCount++;
            }

            if (visibleCount > 0) {
                height += rowHeight;
            }

            int width = targetWidth == Integer.MAX_VALUE
                    ? rowWidth + insets.left + insets.right + getHgap() * 2
                    : targetWidth;
            return new Dimension(width, height + getVgap() + 1);
        }
    }
}
