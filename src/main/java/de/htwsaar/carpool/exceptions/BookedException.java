package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class BookedException extends CarpoolException {
    public BookedException(String userId, Long rideId) {
        this("The user with id " + userId + " already has a booking for ride " + rideId);
    }
    public BookedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
