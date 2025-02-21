package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends CarpoolException {
    public InvalidCredentialsException() {
        this("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
