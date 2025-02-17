package de.htwsaar.carpool.exceptions;

import org.springframework.http.HttpStatus;

/**
 * General exception when a status for any table does not exist.
 */
public class StatusNotFound extends CarpoolException {
    public StatusNotFound(Enum<?> statusEnum) {
        this("Status " + statusEnum.name() + " does not exist in database");
    }
    public StatusNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
