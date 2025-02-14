package de.htwsaar.carpool.handlers;

import de.htwsaar.carpool.domain.ErrorResponse;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.ArrayList;

@Slf4j
@RestControllerAdvice
public class RideServiceExceptionHandler {

    // Add validation exception handler here
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
    MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {

        ArrayList<Object> errorMessage = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.add(error.getDefaultMessage())
        );
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errorMessage.toString()));
    }

    // SQL Exception handler
    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<ErrorResponse>
    SQLExceptionHandler(SQLException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(exception.getMessage()));
    }

    // Add exception handler for all other exceptions here
    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ErrorResponse> RideNotFoundExceptionHandler(RideNotFoundException exception) {
        log.atError().log("Ride not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ErrorResponse> DriverNotFoundExceptionHandler(DriverNotFoundException exception) {
        log.atError().log("Driver not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(UnauthorizedDriverException.class)
    public ResponseEntity<ErrorResponse> UnauthorizedDriverExceptionHandler(UnauthorizedDriverException exception) {
        log.atError().log("Unauthorized driver");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }
}
