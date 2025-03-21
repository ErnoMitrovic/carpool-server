package de.htwsaar.carpool.handlers;

import com.google.firebase.auth.FirebaseAuthException;
import de.htwsaar.carpool.domain.ErrorResponse;
import de.htwsaar.carpool.exceptions.CarpoolException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("General exception occurred: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder().message(ex.getMessage()).build()
        );
    }

    @ExceptionHandler(CarpoolException.class)
    public ResponseEntity<ErrorResponse> handleCarpoolException(CarpoolException ex) {
        log.error("Carpool exception occurred: {}", ex.getMessage());
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

    @ExceptionHandler(FirebaseAuthException.class)
    public ResponseEntity<ErrorResponse> handleFirebaseAuthException(FirebaseAuthException ex) {
        return ResponseEntity.status(ex.getHttpResponse().getStatusCode()).body(
                ErrorResponse.builder().message(ex.getMessage()).build());
    }
}
