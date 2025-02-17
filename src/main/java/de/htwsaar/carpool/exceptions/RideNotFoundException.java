package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class RideNotFoundException extends CarpoolException {
    public RideNotFoundException() {
        this("Rides not found");
    }
    public RideNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
