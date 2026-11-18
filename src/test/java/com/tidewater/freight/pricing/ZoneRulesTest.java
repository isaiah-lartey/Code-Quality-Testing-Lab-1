package com.tidewater.freight.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;

@DisplayName("ZoneRule base costs")
class ZoneRulesTest {

    private static final ZoneRule DOMESTIC = new DomesticZoneRule();
    private static final ZoneRule REGIONAL = new RegionalZoneRule();
    private static final ZoneRule INTERNATIONAL = new InternationalZoneRule();

    static Stream<Arguments> baseCostCases() {
        return Stream.of(
            // DOMESTIC: 5.0/kg, no fee
            Arguments.of(named("DOMESTIC", DOMESTIC), "1", "5.00"),
            Arguments.of(named("DOMESTIC", DOMESTIC), "10", "50.00"),
            Arguments.of(named("DOMESTIC", DOMESTIC), "30", "150.00"),

            // REGIONAL: 8.0/kg, +10 handling
            Arguments.of(named("REGIONAL", REGIONAL), "1", "18.00"),
            Arguments.of(named("REGIONAL", REGIONAL), "5", "50.00"),
            Arguments.of(named("REGIONAL", REGIONAL), "10", "90.00"),

            // INTERNATIONAL: 15.0/kg, +25 customs
            Arguments.of(named("INTERNATIONAL", INTERNATIONAL), "1", "40.00"),
            Arguments.of(named("INTERNATIONAL", INTERNATIONAL), "10", "175.00"),
            Arguments.of(named("INTERNATIONAL", INTERNATIONAL), "40", "625.00")
        );
    }

    @ParameterizedTest(name = "{0} {1}kg -> {2}")
    @DisplayName("base cost is rate x weight plus the zone's fee")
    @MethodSource("baseCostCases")
    void baseCostPerZone(ZoneRule rule, String kg, String expected) {
        BigDecimal cost = rule.baseCost(new BigDecimal(kg));

        assertEquals(0, new BigDecimal(expected).compareTo(cost),
            rule.zoneName() + " " + kg + "kg base cost should be " + expected);
    }

    static Stream<Arguments> zoneNameCases() {
        return Stream.of(
            Arguments.of(named("DomesticZoneRule", DOMESTIC), "DOMESTIC"),
            Arguments.of(named("RegionalZoneRule", REGIONAL), "REGIONAL"),
            Arguments.of(named("InternationalZoneRule", INTERNATIONAL), "INTERNATIONAL")
        );
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @DisplayName("zoneName returns the zone the rule prices")
    @MethodSource("zoneNameCases")
    void zoneNameMatchesTheZoneItPrices(ZoneRule rule, String expectedZoneName) {
        assertEquals(expectedZoneName, rule.zoneName());
    }
}
