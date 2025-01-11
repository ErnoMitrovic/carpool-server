package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ApiResponseDTO;
import de.htwsaar.carpool.domain.request.ride.CreateRideDTO;
import de.htwsaar.carpool.domain.request.ride.RideSortDTO;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface RideService {
    ResponseEntity<ApiResponseDTO<?>> getFilteredRides(RideSortDTO rideSortDTO)
            throws RideNotFoundException;

    ResponseEntity<ApiResponseDTO<?>> createRide(CreateRideDTO createRideDTO);
}
