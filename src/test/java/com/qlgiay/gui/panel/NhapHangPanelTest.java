package com.qlgiay.gui.panel;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NhapHangPanelTest {

    @Test
    void calculateWeightedAverageImportPrice_shouldUseTotalQuantityAsWeight() {
        BigDecimal average = NhapHangPanel.calculateWeightedAverageImportPrice(
                new BigDecimal("2000"), 1,
                new BigDecimal("1000"), 2
        );

        assertEquals(new BigDecimal("1333.33").setScale(2, RoundingMode.HALF_UP), average.setScale(2, RoundingMode.HALF_UP));
    }
}
