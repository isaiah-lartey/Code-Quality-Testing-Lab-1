package com.tidewater.freight.exceptions;

public class UnknownZoneException extends ShippingException {

    public UnknownZoneException(String zone) {
        super("Unknown zone: " + zone);
    }
}