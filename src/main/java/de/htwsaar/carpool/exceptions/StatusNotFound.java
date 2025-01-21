package de.htwsaar.carpool.exceptions;

/**
 * General exception when a status for any table does not exist.
 */
public class StatusNotFound extends RuntimeException {
    public StatusNotFound(String message) {
        super(message);
    }
}
