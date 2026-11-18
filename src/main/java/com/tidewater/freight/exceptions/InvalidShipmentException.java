package com.tidewater.freight.exceptions;

public class InvalidShipmentException extends ShippingException {

    public InvalidShipmentException(String message) {
        super(message);
    }
}