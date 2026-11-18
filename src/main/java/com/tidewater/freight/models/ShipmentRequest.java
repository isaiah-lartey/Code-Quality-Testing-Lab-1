package com.tidewater.freight.models;

import com.tidewater.freight.exceptions.InvalidShipmentException;
import java.math.BigDecimal;

public record ShipmentRequest(String zone, BigDecimal weightKg, ServiceLevel serviceLevel) {

    public ShipmentRequest {
        if (zone == null || zone.isBlank()) {
            throw new InvalidShipmentException("Zone must not be null or blank");
        }
        if (weightKg == null) {
            throw new InvalidShipmentException("Weight must not be null");
        }
        if (weightKg.signum() <= 0) {
            throw new InvalidShipmentException(
                "Weight must be positive, got " + weightKg);
        }
        if (serviceLevel == null) {
            throw new InvalidShipmentException("Service level must not be null");
        }
    }
}