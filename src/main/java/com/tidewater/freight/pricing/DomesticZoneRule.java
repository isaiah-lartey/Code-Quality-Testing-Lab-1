package com.tidewater.freight.pricing;

import java.math.BigDecimal;

public class DomesticZoneRule implements ZoneRule {

    private static final BigDecimal RATE_PER_KG = new BigDecimal("5.0");

    @Override
    public String zoneName() {
        return "DOMESTIC";
    }

    @Override
    public BigDecimal baseCost(BigDecimal weightKg) {
        return RATE_PER_KG.multiply(weightKg);
    }
}