package com.example.air.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PriceCalculator {
    private PriceCalculator() {}

    public static BigDecimal priceForNextSeat(BigDecimal base, int capacity, int seatsSold) {
        if (base == null) throw new IllegalArgumentException("base fiyat boş olamaz");
        if (capacity <= 0) throw new IllegalArgumentException("capacity > 0 olmalı");
        if (seatsSold < 0 || seatsSold >= capacity)
            throw new IllegalArgumentException("seatsSold 0 ile capacity-1 arası olmalı");

        int nextOccupied = seatsSold + 1;

        int step = (nextOccupied - 1) * 10 / capacity;

        BigDecimal factor = BigDecimal.ONE.add(
                BigDecimal.valueOf(step).multiply(BigDecimal.valueOf(0.1))
        );

        return base.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
