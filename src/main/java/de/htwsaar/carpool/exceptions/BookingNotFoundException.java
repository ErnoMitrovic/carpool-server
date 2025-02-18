package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends CarpoolException {
    public BookingNotFoundException(Long userId, Long rideId) {
        this("Booking not found for user " + userId + " and ride " + rideId);
    }

    public BookingNotFoundException(Long rideId) {
        this("Bookings not found for ride " + rideId);
    }

    public BookingNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
