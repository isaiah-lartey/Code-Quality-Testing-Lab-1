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
        ShippingCalculator calculator =
            new ShippingCalculator(repository, new HeavyItemSurcharge());

        printCost(calculator, "DOMESTIC", "10", ServiceLevel.STANDARD);
        printCost(calculator, "INTERNATIONAL", "40", ServiceLevel.EXPRESS);
        printCost(calculator, "MOON", "10", ServiceLevel.STANDARD);
    }

    private static void printCost(ShippingCalculator calculator, String zone,
                                  String weightKg, ServiceLevel level) {
        try {
            ShipmentRequest request = new ShipmentRequest(zone, new BigDecimal(weightKg), level);
            System.out.println(calculator.calculateCost(request));
        } catch (ShippingException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
