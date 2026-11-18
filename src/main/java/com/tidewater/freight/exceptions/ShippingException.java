package com.tidewater.freight.exceptions;

public abstract class ShippingException extends RuntimeException {

    protected ShippingException(String message) {
        super(message);
    }
}