package com.tidewater.freight.pricing;

import java.math.BigDecimal;

public class InternationalZoneRule implements ZoneRule {

    private static final BigDecimal RATE_PER_KG = new BigDecimal("15.0");
    private static final BigDecimal CUSTOMS_FEE = new BigDecimal("25");

    @Override
    public String zoneName() {
        return "INTERNATIONAL";
    }

    @Override
    public BigDecimal baseCost(BigDecimal weightKg) {
        return RATE_PER_KG.multiply(weightKg).add(CUSTOMS_FEE);
    }
}