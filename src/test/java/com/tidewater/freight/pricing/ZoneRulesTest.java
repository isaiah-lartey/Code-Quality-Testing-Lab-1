package com.tidewater.freight.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ZoneRule base costs")
class ZoneRulesTest {

    @ParameterizedTest(name = "{0} {1}kg -> {2}")
    @DisplayName("base cost is rate x weight plus the zone's fee")
    @CsvSource({
        // DOMESTIC: 5.0/kg, no fee
        "DOMESTIC,       1,    5.00",
        "DOMESTIC,      10,   50.00",
        "DOMESTIC,      30,  150.00",

        // REGIONAL: 8.0/kg, +10 handling
        "REGIONAL,       1,   18.00",
        "REGIONAL,       5,   50.00",
        "REGIONAL,      10,   90.00",

        // INTERNATIONAL: 15.0/kg, +25 customs
        "INTERNATIONAL,  1,   40.00",
        "INTERNATIONAL, 10,  175.00",
        "INTERNATIONAL, 40,  625.00"
    })
    void baseCostPerZone(String zone, String kg, String expected) {
        ZoneRule rule = ruleFor(zone);
        BigDecimal cost = rule.baseCost(new BigDecimal(kg));

        assertEquals(0, new BigDecimal(expected).compareTo(cost),
            zone + " " + kg + "kg base cost should be " + expected);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @DisplayName("zoneName returns the zone the rule prices")
    @CsvSource({
        "DOMESTIC,      DOMESTIC",
        "REGIONAL,      REGIONAL",
        "INTERNATIONAL, INTERNATIONAL"
    })
    void zoneNameMatchesTheZoneItPrices(String expectedName, String ruleName) {
        ZoneRule rule = ruleFor(ruleName);
        assertEquals(expectedName, rule.zoneName());
    }

    private static ZoneRule ruleFor(String zone) {
        switch (zone) {
            case "DOMESTIC":      return new DomesticZoneRule();
            case "REGIONAL":      return new RegionalZoneRule();
            case "INTERNATIONAL": return new InternationalZoneRule();
            default: throw new IllegalArgumentException("Unknown zone: " + zone);
        }
    }
}