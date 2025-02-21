package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidBookingException extends CarpoolException {
    public InvalidBookingException() {
        this("Booking does not belong to the specified ride");
    }
    public InvalidBookingException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
