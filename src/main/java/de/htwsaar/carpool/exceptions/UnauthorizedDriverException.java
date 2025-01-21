package de.htwsaar.carpool.exceptions;

// TODO: Implement authorization for drivers
public class UnauthorizedDriverException extends RuntimeException {
    public UnauthorizedDriverException(String message) {
        super(message);
    }
}

