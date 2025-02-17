package de.htwsaar.carpool.handlers;

import de.htwsaar.carpool.domain.ErrorResponse;
import de.htwsaar.carpool.exceptions.CarpoolException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder().message(ex.getMessage()).build()
        );
    }

    @ExceptionHandler(CarpoolException.class)
    public ResponseEntity<ErrorResponse> handleCarpoolException(CarpoolException ex) {
        return ResponseEntity.status(ex.getErrorStatus()).body(
                ErrorResponse.builder().message(ex.getMessage()).build()
        );
    }

    /**
     * This is one of the security exception handlers for global spring behavior
     * @param ex exception mapped
     * @return the error response with the message that cause the error
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder().message(ex.getMessage()).build());
    }
}
