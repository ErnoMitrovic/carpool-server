package de.htwsaar.carpool.exceptions;

public class RideNotFoundException extends Exception {
    public RideNotFoundException() {
        this("Rides not found");
    }
    public RideNotFoundException(String message) {
        super(message);
    }
}
