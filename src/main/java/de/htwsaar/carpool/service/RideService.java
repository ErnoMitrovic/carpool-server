package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.UpdateRideRequest;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RideService {
    /**
     * As a user, I want to search for available carpool rides based on my destination, date, and time
     * @param getRidesRequest GetRidesRequest DTO
     * @return ResponseEntity<List<RideResponse>> List of RideResponse
     * @throws RideNotFoundException RideNotFoundException if no rides are found
     */
    ResponseEntity<List<RideResponse>> getFilteredRides(GetRidesRequest getRidesRequest)
            throws RideNotFoundException;

    /**
     * As a driver, I want to create a carpool ride with details like departure time, destination, available seats,
     * @param createRideRequest CreateRideRequest DTO
     * @return ResponseEntity<RideResponse> RideResponse DTO
     * @throws DriverNotFoundException DriverNotFoundException if driver is not found
     */
    @Transactional
    ResponseEntity<RideResponse> createRide(CreateRideRequest createRideRequest)
            throws DriverNotFoundException;

    /**
     * As a driver, I want to update the details of a carpool ride that I have created.
     * If a location is updated, and it doesn't exist in the database, it should be added.
     * @param rideId Ride ID
     * @param updateRideRequest UpdateRideRequest DTO
     * @return ResponseEntity<RideResponse> RideResponse DTO
     * @throws RideNotFoundException RideNotFoundException if ride is not found
     */
    @Transactional
    ResponseEntity<RideResponse> updateRide(Long rideId, @Valid UpdateRideRequest updateRideRequest)
            throws RideNotFoundException, UnauthorizedDriverException;

    /**
     * As a driver, I want to delete a carpool ride that I have created.
     * @param rideId Ride ID
     * @return ResponseEntity<Long> Ride ID
     * @throws RideNotFoundException RideNotFoundException if ride is not found
     * @throws UnauthorizedDriverException UnauthorizedDriverException if driver is not authorized
     */
    ResponseEntity<Long> deleteRide(Long rideId, Long driverId) // TODO: Get rider ID from token
            throws RideNotFoundException, UnauthorizedDriverException;
}
