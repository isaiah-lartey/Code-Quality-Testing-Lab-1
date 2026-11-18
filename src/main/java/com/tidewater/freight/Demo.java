package com.tidewater.freight;

import com.tidewater.freight.exceptions.ShippingException;
import com.tidewater.freight.models.ServiceLevel;
import com.tidewater.freight.models.ShipmentRequest;
import com.tidewater.freight.pricing.DomesticZoneRule;
import com.tidewater.freight.pricing.HeavyItemSurcharge;
import com.tidewater.freight.pricing.InternationalZoneRule;
import com.tidewater.freight.pricing.RegionalZoneRule;
import com.tidewater.freight.repositories.InMemoryZoneRuleRepository;
import com.tidewater.freight.repositories.ZoneRuleRepository;

import java.math.BigDecimal;
import java.util.List;

public class Demo {

    public static void main(String[] args) {
        ZoneRuleRepository repository = new InMemoryZoneRuleRepository(List.of(
            new DomesticZoneRule(),
            new RegionalZoneRule(),
            new InternationalZoneRule()
        ));
        ShippingCalculator calculator = new ShippingCalculator(repository, new HeavyItemSurcharge());

        System.out.println(calculator.calculateCost(new ShipmentRequest("DOMESTIC", new BigDecimal("10"), ServiceLevel.STANDARD)));

        System.out.println(calculator.calculateCost(new ShipmentRequest("INTERNATIONAL", new BigDecimal("40"), ServiceLevel.EXPRESS)));

        try {
            calculator.calculateCost(new ShipmentRequest("MOON", new BigDecimal("10"), ServiceLevel.STANDARD));
        } catch (ShippingException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}