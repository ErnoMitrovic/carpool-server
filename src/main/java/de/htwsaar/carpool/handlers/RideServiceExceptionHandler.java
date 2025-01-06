package de.htwsaar.carpool.handlers;

import de.htwsaar.carpool.dto.ApiResponseDTO;
import de.htwsaar.carpool.dto.ApiResponseStatus;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RideServiceExceptionHandler {

    // Add validation exception handler here
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<?>>
    MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {

        ArrayList<Object> errorMessage = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.add(error.getDefaultMessage());
        });
        return ResponseEntity.badRequest()
                .body(new ApiResponseDTO<>(ApiResponseStatus.FAIL, errorMessage.toString()));
    }

    // Add exception handler for all other exceptions here
    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<?>> RideNotFoundExceptionHandler(RideNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<>(ApiResponseStatus.FAIL, exception.getMessage()));
    }
}
