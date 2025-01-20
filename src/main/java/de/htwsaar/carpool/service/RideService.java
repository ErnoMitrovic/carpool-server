package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RideService {
    ResponseEntity<List<RideResponse>> getFilteredRides(GetRidesRequest getRidesRequest)
            throws RideNotFoundException;

    @Transactional
    ResponseEntity<RideResponse> createRide(CreateRideRequest createRideRequest)
            throws DriverNotFoundException;
}
