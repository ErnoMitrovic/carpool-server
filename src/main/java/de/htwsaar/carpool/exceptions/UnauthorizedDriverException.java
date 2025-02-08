package de.htwsaar.carpool.exceptions;

public class UnauthorizedDriverException extends RuntimeException {
    public UnauthorizedDriverException(String message) {
        super(message);
    }
}

