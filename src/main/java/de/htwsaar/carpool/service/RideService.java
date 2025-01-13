package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ApiResponseDTO;
import de.htwsaar.carpool.domain.request.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.request.ride.GetRidesRequest;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface RideService {
    ResponseEntity<ApiResponseDTO<?>> getFilteredRides(GetRidesRequest getRidesRequest)
            throws RideNotFoundException;

    ResponseEntity<ApiResponseDTO<?>> createRide(CreateRideRequest createRideRequest)
            throws DriverNotFoundException;
}
