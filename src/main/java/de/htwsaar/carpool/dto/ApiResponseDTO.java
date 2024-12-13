package de.htwsaar.carpool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private ApiResponseStatus status;
    private T response;
}
