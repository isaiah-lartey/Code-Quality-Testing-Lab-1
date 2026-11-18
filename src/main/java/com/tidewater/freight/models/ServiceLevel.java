package com.tidewater.freight.models;

import java.math.BigDecimal;

public enum ServiceLevel {
    STANDARD(BigDecimal.ONE),
    EXPRESS(new BigDecimal("1.5"));

    private final BigDecimal multiplier;

    ServiceLevel(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public BigDecimal apply(BigDecimal cost) {
        return cost.multiply(multiplier);
    }
}