package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import de.htwsaar.carpool.domain.location.LocationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {
    ResponseEntity<LocationResponse> saveLocation(CreateLocationRequest locationRequest);
}
