package de.htwsaar.carpool.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        this("User with id " + id + " not found");
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
