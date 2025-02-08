package de.htwsaar.carpool.handlers;

import de.htwsaar.carpool.domain.ErrorResponse;
import de.htwsaar.carpool.exceptions.EmailExistsException;
import de.htwsaar.carpool.exceptions.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(value = EmailExistsException.class)
    public ResponseEntity<ErrorResponse> EmailExistsExceptionHandler(EmailExistsException exception) {
        log.atError().log("Email already exists");
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> InvalidCredentialsExceptionHandler(InvalidCredentialsException exception) {
        log.atError().log("Invalid credentials");
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }
}
