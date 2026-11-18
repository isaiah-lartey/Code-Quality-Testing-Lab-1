package com.tidewater.freight.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HeavyItemSurcharge")
class HeavyItemSurchargeTest {

    @ParameterizedTest(name = "{0}kg -> {1}")
    @DisplayName("30kg and below is not heavy; above 30kg adds 20")
    @CsvSource({
        "0.01,   0",
        "1,      0",
        "29.99,  0",
        "30,     0",     // boundary: 30 is NOT heavy
        "30.01,  20",    // just above
        "31,     20",
        "1000,   20"
    })
    void surchargeBoundaryAroundThirtyKg(String kg, String expected) {
        BigDecimal surcharge = new HeavyItemSurcharge()
            .surchargeFor(new BigDecimal(kg));

        assertEquals(0, new BigDecimal(expected).compareTo(surcharge),
            "surcharge for " + kg + "kg should be " + expected);
    }
}