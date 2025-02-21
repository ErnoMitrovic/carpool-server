package de.htwsaar.carpool.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CarpoolException extends RuntimeException {
    private final HttpStatus errorStatus;

    public CarpoolException(String message, HttpStatus errorStatus) {
        super(message);
        this.errorStatus = errorStatus;
    }
}
