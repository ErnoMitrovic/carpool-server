package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends CarpoolException {
    public UnauthorizedUserException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedUserException(String userId, Long bookingId) {
        super("User " + userId + " is not authorized to update booking " + bookingId, HttpStatus.UNAUTHORIZED);
    }
}
