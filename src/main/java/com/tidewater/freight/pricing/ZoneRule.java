package com.tidewater.freight.pricing;

import java.math.BigDecimal;

public interface ZoneRule {

    String zoneName();

    BigDecimal baseCost(BigDecimal weightKg);
}