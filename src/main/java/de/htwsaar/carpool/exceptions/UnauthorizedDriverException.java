package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedDriverException extends CarpoolException {
    public UnauthorizedDriverException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

