package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CarpoolException {
    public UserNotFoundException(long id) {
        this("User with id " + id + " not found");
    }

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
