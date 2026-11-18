package com.tidewater.freight.pricing;

import java.math.BigDecimal;

public class RegionalZoneRule implements ZoneRule {

    private static final BigDecimal RATE_PER_KG = new BigDecimal("8.0");
    private static final BigDecimal HANDLING_FEE = new BigDecimal("10");

    @Override
    public String zoneName() {
        return "REGIONAL";
    }

    @Override
    public BigDecimal baseCost(BigDecimal weightKg) {
        return RATE_PER_KG.multiply(weightKg).add(HANDLING_FEE);
    }
}