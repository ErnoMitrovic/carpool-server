package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedDriverException extends CarpoolException {
    public UnauthorizedDriverException(Long userId, Long rideId) {
        super("User with id " + userId + " is not the driver of ride with id " + rideId, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedDriverException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

