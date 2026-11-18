package com.tidewater.freight;

import com.tidewater.freight.models.ServiceLevel;
import com.tidewater.freight.models.ShipmentRequest;
import com.tidewater.freight.pricing.DomesticZoneRule;
import com.tidewater.freight.pricing.HeavyItemSurcharge;
import com.tidewater.freight.pricing.InternationalZoneRule;
import com.tidewater.freight.pricing.RegionalZoneRule;
import com.tidewater.freight.repositories.InMemoryZoneRuleRepository;
import com.tidewater.freight.repositories.ZoneRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ShippingCalculator composed price")
class ShippingPricingTest {

    private ShippingCalculator calculator;

    @BeforeEach
    void setUp() {
        ZoneRuleRepository repository = new InMemoryZoneRuleRepository(List.of(
            new DomesticZoneRule(),
            new RegionalZoneRule(),
            new InternationalZoneRule()
        ));
        calculator = new ShippingCalculator(repository, new HeavyItemSurcharge());
    }

    @ParameterizedTest(name = "{0} {1}kg {2} = {3}")
    @DisplayName("exact composed price for zone, weight and service level")
    @CsvSource({
        // base rates, no surcharge, no multiplier
        "DOMESTIC,      10, STANDARD,   50.00",   // 5x10
        "REGIONAL,       5, STANDARD,   50.00",   // 8x5 + 10
        "INTERNATIONAL, 10, STANDARD,  175.00",   // 15x10 + 25

        // express multiplier on the base (and the fee)
        "DOMESTIC,      10, EXPRESS,    75.00",   // 50 x 1.5
        "INTERNATIONAL, 40, EXPRESS,   967.50",   // (15x40 + 25) x 1.5

        // 30kg boundary: at 30 no surcharge, at 31 surcharge applies
        "DOMESTIC,      30, STANDARD,  150.00",   // 150, no surcharge
        "DOMESTIC,      31, STANDARD,  175.00",   // 155 + 20

        // surcharge-then-multiplier ordering: express multiplies the surcharge
        "DOMESTIC,      31, EXPRESS,   262.50"    // (155 + 20) x 1.5
    })
    void exactComposedPrice(String zone, String kg,
                            ServiceLevel level, String expected) {
        BigDecimal cost = calculator.calculateCost(
            new ShipmentRequest(zone, new BigDecimal(kg), level));

        assertEquals(new BigDecimal(expected), cost,
            zone + " " + kg + "kg " + level + " should be " + expected);
    }

    @Test
    @DisplayName("unknown zone is rejected end to end")
    void unknownZoneIsRejectedEndToEnd() {
        assertThrows(com.tidewater.freight.exceptions.UnknownZoneException.class, () ->
            calculator.calculateCost(
                new ShipmentRequest("MOON", new BigDecimal("10"), ServiceLevel.STANDARD)));
    }

    @Test
    @DisplayName("lowercase zone is rejected (zone names are case-sensitive)")
    void lowercaseZoneIsRejected() {
        assertThrows(com.tidewater.freight.exceptions.UnknownZoneException.class, () ->
            calculator.calculateCost(
                new ShipmentRequest("domestic", new BigDecimal("10"), ServiceLevel.STANDARD)));
    }
}