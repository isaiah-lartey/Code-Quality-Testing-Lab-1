package com.tidewater.freight.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ServiceLevel")
class ServiceLevelTest {

    @ParameterizedTest(name = "{0} x {1} = {2}")
    @DisplayName("STANDARD multiplies by 1; EXPRESS multiplies by 1.5")
    @CsvSource({
        "STANDARD, 100,    100",
        "STANDARD, 100.00, 100.00",
        "EXPRESS,  100,    150",
        "EXPRESS,  100.00, 150.00",
        "EXPRESS,  33.33,  49.995"
    })
    void applyMultipliesByTheLevelsRate(ServiceLevel level,
                                        String input,
                                        String expected) {
        BigDecimal result = level.apply(new BigDecimal(input));

        assertEquals(0, new BigDecimal(expected).compareTo(result),
            level + " applied to " + input + " should be " + expected);
    }
}