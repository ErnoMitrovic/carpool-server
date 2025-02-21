package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class UnavailableSeatsException extends CarpoolException {
    public UnavailableSeatsException() {
        this("This ride doesnt have any more seats");
    }
    public UnavailableSeatsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
