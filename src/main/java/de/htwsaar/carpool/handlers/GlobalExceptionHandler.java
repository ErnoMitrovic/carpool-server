package de.htwsaar.carpool.handlers;

import de.htwsaar.carpool.domain.ErrorResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Profile("prod")
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder().message(ex.getMessage()).build()
        );
    }
}
