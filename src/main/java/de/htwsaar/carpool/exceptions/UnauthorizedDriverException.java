package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedDriverException extends CarpoolException {
    public UnauthorizedDriverException(String userId, Long rideId) {
        this("User with id " + userId + " is not the driver of ride with id " + rideId);
    }

    public UnauthorizedDriverException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

