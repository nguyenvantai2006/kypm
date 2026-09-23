package com.qlgiay.gui.panel;

import com.qlgiay.dto.SanPhamDTO;
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

    @Test
    void shouldKeepSalePriceWhenNewImportDoesNotIncreaseAboveCurrentImport() {
        BigDecimal giaBan = SanPhamDTO.tinhGiaBanTheoChinhSachGiaNhap(
                new BigDecimal("1000000"),
                new BigDecimal("1250000"),
                new BigDecimal("1500000"),
                new BigDecimal("20")
        );

        assertEquals(new BigDecimal("1500000"), giaBan);
    }

    @Test
    void shouldIncreaseSalePriceWhenNewImportIncreases() {
        BigDecimal giaBan = SanPhamDTO.tinhGiaBanTheoChinhSachGiaNhap(
                new BigDecimal("1500000"),
                new BigDecimal("1250000"),
                new BigDecimal("1500000"),
                new BigDecimal("20")
        );

        assertEquals(new BigDecimal("1800000"), giaBan);
    }
}
