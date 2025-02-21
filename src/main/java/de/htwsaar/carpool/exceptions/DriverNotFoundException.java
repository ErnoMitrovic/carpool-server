package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class DriverNotFoundException extends CarpoolException {

    public DriverNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
