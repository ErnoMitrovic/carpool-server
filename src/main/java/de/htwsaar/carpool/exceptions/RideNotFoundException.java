package de.htwsaar.carpool.exceptions;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException() {
        this("Rides not found");
    }
    public RideNotFoundException(String message) {
        super(message);
    }
}
