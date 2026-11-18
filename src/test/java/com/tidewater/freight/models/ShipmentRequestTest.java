package com.tidewater.freight.models;

import com.tidewater.freight.exceptions.InvalidShipmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ShipmentRequest validation")
class ShipmentRequestTest {

    @Test
    @DisplayName("valid request keeps its values")
    void validRequestKeepsItsValues() {
        ShipmentRequest request = new ShipmentRequest(
            "DOMESTIC", new BigDecimal("10.5"), ServiceLevel.EXPRESS);

        assertEquals("DOMESTIC", request.zone());
        assertEquals(new BigDecimal("10.5"), request.weightKg());
        assertEquals(ServiceLevel.EXPRESS, request.serviceLevel());
    }

    @Test
    @DisplayName("null zone is rejected")
    void rejectsNullZone() {
        InvalidShipmentException ex = assertThrows(InvalidShipmentException.class, () ->
            new ShipmentRequest(null, new BigDecimal("10"), ServiceLevel.STANDARD));
        assertEquals("Zone must not be null or blank", ex.getMessage());
    }

    @Test
    @DisplayName("blank zone is rejected")
    void rejectsBlankZone() {
        InvalidShipmentException ex = assertThrows(InvalidShipmentException.class, () ->
            new ShipmentRequest("   ", new BigDecimal("10"), ServiceLevel.STANDARD));
        assertEquals("Zone must not be null or blank", ex.getMessage());
    }

    @Test
    @DisplayName("null weight is rejected")
    void rejectsNullWeight() {
        InvalidShipmentException ex = assertThrows(InvalidShipmentException.class, () ->
            new ShipmentRequest("DOMESTIC", null, ServiceLevel.STANDARD));
        assertEquals("Weight must not be null", ex.getMessage());
    }

    @Test
    @DisplayName("zero weight is rejected")
    void rejectsZeroWeight() {
        InvalidShipmentException ex = assertThrows(InvalidShipmentException.class, () ->
            new ShipmentRequest("DOMESTIC", BigDecimal.ZERO, ServiceLevel.STANDARD));
        assertEquals("Weight must be positive, got 0", ex.getMessage());
    }

    @Test
    @DisplayName("negative weight is rejected")
    void rejectsNegativeWeight() {
        InvalidShipmentException ex = assertThrows(InvalidShipmentException.class, () ->
            new ShipmentRequest("DOMESTIC", new BigDecimal("-0.01"), ServiceLevel.STANDARD));
        assertEquals("Weight must be positive, got -0.01", ex.getMessage());
    }

    @Test
    @DisplayName("null service level is rejected")
    void rejectsNullServiceLevel() {
        InvalidShipmentException ex = assertThrows(InvalidShipmentException.class, () ->
            new ShipmentRequest("DOMESTIC", new BigDecimal("10"), null));
        assertEquals("Service level must not be null", ex.getMessage());
    }

    @Test
    @DisplayName("smallest positive weight 0.01 is accepted")
    void acceptsSmallestPositiveWeight() {
        assertDoesNotThrow(() ->
            new ShipmentRequest("DOMESTIC", new BigDecimal("0.01"), ServiceLevel.STANDARD));
    }

    @Test
    @DisplayName("surrounding whitespace in the zone is trimmed")
    void trimsSurroundingWhitespaceFromZone() {
        ShipmentRequest request = new ShipmentRequest(
            "  DOMESTIC ", new BigDecimal("10"), ServiceLevel.STANDARD);

        assertEquals("DOMESTIC", request.zone());
    }
}
