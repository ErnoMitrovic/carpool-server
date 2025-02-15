package de.htwsaar.carpool.exceptions;

public class BookedException extends IllegalStateException {
    public BookedException(Long userId, Long rideId) {
        this("The user with id " + userId + " already has a booking for ride " + rideId);
    }
    public BookedException(String message) {
        super(message);
    }
}
