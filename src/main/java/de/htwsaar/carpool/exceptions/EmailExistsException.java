package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

public class EmailExistsException extends CarpoolException {
    public EmailExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
