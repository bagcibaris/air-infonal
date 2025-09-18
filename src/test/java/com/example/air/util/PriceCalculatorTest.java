package com.example.air.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    @Test
    void tenSeat_aircraft_blocksOf10Percent() {
        BigDecimal base = new BigDecimal("100");

        assertEquals(new BigDecimal("100.00"), PriceCalculator.priceForNextSeat(base, 10, 0)); // 1. yolcu
        assertEquals(new BigDecimal("110.00"), PriceCalculator.priceForNextSeat(base, 10, 1)); // 2. yolcu
        assertEquals(new BigDecimal("190.00"), PriceCalculator.priceForNextSeat(base, 10, 9)); // 10. yolcu
    }

    @Test
    void hundredSeat_aircraft_blocksCorrect() {
        BigDecimal base = new BigDecimal("100");

        assertEquals(new BigDecimal("100.00"), PriceCalculator.priceForNextSeat(base, 100, 0));  // #1
        assertEquals(new BigDecimal("100.00"), PriceCalculator.priceForNextSeat(base, 100, 9));  // #10
        assertEquals(new BigDecimal("110.00"), PriceCalculator.priceForNextSeat(base, 100, 10)); // #11
        assertEquals(new BigDecimal("120.00"), PriceCalculator.priceForNextSeat(base, 100, 20)); // #21
        assertEquals(new BigDecimal("190.00"), PriceCalculator.priceForNextSeat(base, 100, 99)); // #100
    }

    @Test
    void twoHundredSeat_aircraft_stepStartsAfter20th() {
        BigDecimal base = new BigDecimal("100");

        assertEquals(new BigDecimal("100.00"), PriceCalculator.priceForNextSeat(base, 200, 0));   // #1
        assertEquals(new BigDecimal("100.00"), PriceCalculator.priceForNextSeat(base, 200, 19));  // #20
        assertEquals(new BigDecimal("110.00"), PriceCalculator.priceForNextSeat(base, 200, 20));  // #21
    }

    @Test
    void invalidArguments_throw() {
        assertThrows(IllegalArgumentException.class, () -> PriceCalculator.priceForNextSeat(null, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> PriceCalculator.priceForNextSeat(new BigDecimal("100"), 0, 0));
        assertThrows(IllegalArgumentException.class, () -> PriceCalculator.priceForNextSeat(new BigDecimal("100"), 10, -1));
        assertThrows(IllegalArgumentException.class, () -> PriceCalculator.priceForNextSeat(new BigDecimal("100"), 10, 10));
    }
}
