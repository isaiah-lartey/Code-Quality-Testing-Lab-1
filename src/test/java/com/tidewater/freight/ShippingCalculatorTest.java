package com.tidewater.freight;

import com.tidewater.freight.exceptions.UnknownZoneException;
import com.tidewater.freight.models.ServiceLevel;
import com.tidewater.freight.models.ShipmentRequest;
import com.tidewater.freight.pricing.HeavyItemSurcharge;
import com.tidewater.freight.pricing.ZoneRule;
import com.tidewater.freight.repositories.ZoneRuleRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingCalculatorTest {

    @Mock
    ZoneRuleRepository repository;

    @Test
    @DisplayName("Unknown zone throws UnknownZoneException")
    void unknownZoneThrows() {
        when(repository.findRule("MOON")).thenReturn(Optional.empty());
        ShippingCalculator calculator =
            new ShippingCalculator(repository, new HeavyItemSurcharge());
        ShipmentRequest request =
            new ShipmentRequest("MOON", new BigDecimal("10"), ServiceLevel.STANDARD);

        UnknownZoneException ex = assertThrows(UnknownZoneException.class,
            () -> calculator.calculateCost(request));

        assertEquals("Unknown zone: MOON", ex.getMessage());
    }

    @Test
    @DisplayName("Cost is rounded to two decimal places")
    void roundsToTwoDecimals() {
        ZoneRule stubRule = mock(ZoneRule.class);
        when(stubRule.baseCost(any())).thenReturn(new BigDecimal("5.005"));
        when(repository.findRule("DOMESTIC")).thenReturn(Optional.of(stubRule));

        ShippingCalculator calculator =
            new ShippingCalculator(repository, new HeavyItemSurcharge());
        ShipmentRequest request =
            new ShipmentRequest("DOMESTIC", new BigDecimal("1"), ServiceLevel.STANDARD);

        BigDecimal cost = calculator.calculateCost(request);

        assertEquals(new BigDecimal("5.01"), cost);
    }
}