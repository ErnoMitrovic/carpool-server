package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ApiResponseDTO;
import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {
    ResponseEntity<ApiResponseDTO<?>> saveLocation(CreateLocationRequest locationRequest);
}
