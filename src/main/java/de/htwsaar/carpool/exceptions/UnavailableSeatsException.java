package de.htwsaar.carpool.exceptions;

public class UnavailableSeatsException extends IllegalStateException {
    public UnavailableSeatsException() {
        this("This ride doesnt have any more seats");
    }
    public UnavailableSeatsException(String message) {
        super(message);
    }
}
