package com.tidewater.freight.pricing;

import java.math.BigDecimal;

public class HeavyItemSurcharge {

    private static final BigDecimal HEAVY_THRESHOLD_KG = new BigDecimal("30");
    private static final BigDecimal SURCHARGE = new BigDecimal("20");

    public BigDecimal surchargeFor(BigDecimal weightKg) {
        if (weightKg.compareTo(HEAVY_THRESHOLD_KG) > 0) {
            return SURCHARGE;
        }
        return BigDecimal.ZERO;
    }
}