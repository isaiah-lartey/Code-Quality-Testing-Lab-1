package com.tidewater.freight;

import com.tidewater.freight.exceptions.UnknownZoneException;
import com.tidewater.freight.models.ShipmentRequest;
import com.tidewater.freight.pricing.HeavyItemSurcharge;
import com.tidewater.freight.pricing.ZoneRule;
import com.tidewater.freight.repositories.ZoneRuleRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShippingCalculator {

    private final ZoneRuleRepository zoneRules;
    private final HeavyItemSurcharge heavySurcharge;

    public ShippingCalculator(ZoneRuleRepository zoneRules, HeavyItemSurcharge heavySurcharge) {
        this.zoneRules = zoneRules;
        this.heavySurcharge = heavySurcharge;
    }

    public BigDecimal calculateCost(ShipmentRequest request) {
        ZoneRule rule = zoneRules.findRule(request.zone()).orElseThrow(() -> new UnknownZoneException(request.zone()));

        BigDecimal cost = rule.baseCost(request.weightKg());
        cost = cost.add(heavySurcharge.surchargeFor(request.weightKg()));
        cost = request.serviceLevel().apply(cost);

        return cost.setScale(2, RoundingMode.HALF_UP);
    }
}