package com.qlgiay.util;

import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ScrollUtil {
    public static void applySmoothScroll(JScrollPane sp) {
        sp.setWheelScrollingEnabled(false);

        sp.addMouseWheelListener(new MouseWheelListener() {
            private double accumulated = 0;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar vertical = sp.getVerticalScrollBar();
                if (!vertical.isVisible() || e.isShiftDown()) {
                    return;
                }

                double preciseWheelRotation = e.getPreciseWheelRotation();
                int speed = 30;

                accumulated += preciseWheelRotation * speed;
                int delta = (int) accumulated;

                if (delta != 0) {
                    accumulated -= delta;
                    int newValue = vertical.getValue() + delta;
                    newValue = Math.max(vertical.getMinimum(),
                            Math.min(newValue, vertical.getMaximum() - vertical.getVisibleAmount()));
                    vertical.setValue(newValue);
                }
            }
        });

        sp.getVerticalScrollBar().setUnitIncrement(16);
    }
}
