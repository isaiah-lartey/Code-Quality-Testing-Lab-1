package com.tidewater.freight;

import com.tidewater.freight.models.ServiceLevel;
import com.tidewater.freight.models.ShipmentRequest;
import com.tidewater.freight.pricing.DomesticZoneRule;
import com.tidewater.freight.pricing.HeavyItemSurcharge;
import com.tidewater.freight.pricing.ZoneRule;
import com.tidewater.freight.repositories.InMemoryZoneRuleRepository;
import com.tidewater.freight.repositories.ZoneRuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("The design absorbs the coming new zone")
class NewZoneExtensionTest {

    static class ArcticZoneRule implements ZoneRule {

        @Override
        public String zoneName() {
            return "ARCTIC";
        }

        @Override
        public BigDecimal baseCost(BigDecimal weightKg) {
            return new BigDecimal("20").multiply(weightKg)
                .add(new BigDecimal("50"));
        }
    }

    @Test
    @DisplayName("a new zone is priced with no change to existing classes")
    void newZoneIsPricedWithNoChangeToExistingClasses() {
        ZoneRuleRepository repository = new InMemoryZoneRuleRepository(List.of(
            new DomesticZoneRule(),
            new ArcticZoneRule()          
        ));
        ShippingCalculator calculator =
            new ShippingCalculator(repository, new HeavyItemSurcharge());

        BigDecimal cost = calculator.calculateCost(
            new ShipmentRequest("ARCTIC", new BigDecimal("2"), ServiceLevel.STANDARD));

        assertEquals(new BigDecimal("90.00"), cost);
    }

    @Test
    @DisplayName("registering a new zone does not change existing zones' prices")
    void existingZonesAreUnchangedAfterAddingANewZone() {
        ZoneRuleRepository before = new InMemoryZoneRuleRepository(List.of(
            new DomesticZoneRule()
        ));
        ZoneRuleRepository after = new InMemoryZoneRuleRepository(List.of(
            new DomesticZoneRule(),
            new ArcticZoneRule()
        ));

        ShippingCalculator calcBefore = new ShippingCalculator(before, new HeavyItemSurcharge());
        ShippingCalculator calcAfter  = new ShippingCalculator(after,  new HeavyItemSurcharge());

        ShipmentRequest domestic = new ShipmentRequest(
            "DOMESTIC", new BigDecimal("10"), ServiceLevel.STANDARD);

        assertEquals(calcBefore.calculateCost(domestic), calcAfter.calculateCost(domestic));
    }
}