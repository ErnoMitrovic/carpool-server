package de.htwsaar.carpool.handlers;

import de.htwsaar.carpool.domain.ErrorResponse;
import de.htwsaar.carpool.exceptions.BookedException;
import de.htwsaar.carpool.exceptions.UnavailableSeatsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookingHandler {

    @ExceptionHandler(UnavailableSeatsException.class)
    public ResponseEntity<ErrorResponse> handleUnavailableSeats(UnavailableSeatsException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder().message(ex.getMessage()).build()
        );
    }

    @ExceptionHandler(BookedException.class)
    public ResponseEntity<ErrorResponse> handleBooked(UnavailableSeatsException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder().message(ex.getMessage()).build()
        );
    }
}
