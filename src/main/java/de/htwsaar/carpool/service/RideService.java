package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.UpdateRideRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RideService {
    /**
     * As a user, I want to search for available carpool rides based on my destination, date, and time
     *
     * @param getRidesRequest GetRidesRequest DTO
     * @return ResponseEntity<List < RideResponse>> List of RideResponse
     */
    ResponseEntity<List<RideResponse>> getFilteredRides(GetRidesRequest getRidesRequest);

    /**
     * As a driver, I want to create a carpool ride with details like departure time, destination, available seats,
     *
     * @param createRideRequest CreateRideRequest DTO
     * @param driverId          the driver that owns this ride
     * @return ResponseEntity<RideResponse> RideResponse DTO
     */
    @Transactional
    ResponseEntity<RideResponse> createRide(CreateRideRequest createRideRequest, String driverId);

    /**
     * As a driver, I want to update the details of a carpool ride that I have created.
     * If a location is updated, and it doesn't exist in the database, it should be added.
     *
     * @param rideId            Ride ID
     * @param updateRideRequest UpdateRideRequest DTO
     * @param driverId          the driver that owns this ride
     * @return ResponseEntity<RideResponse> RideResponse DTO
     */
    @Transactional
    ResponseEntity<RideResponse> updateRide(Long rideId, @Valid UpdateRideRequest updateRideRequest, String driverId);

    /**
     * As a driver, I want to cancel (delete) a carpool ride that I have created.
     *
     * @param rideId Ride ID
     * @return ResponseEntity<Long> Ride ID
     */
    @Transactional
    ResponseEntity<Void> cancelRide(Long rideId, String driverId);

    /**
     * As a driver, I want to get all carpool rides created by me.
     *
     * @param driverId Driver ID
     * @param sort     Sort
     * @return ResponseEntity<List < RideResponse>> List of RideResponse
     */
    ResponseEntity<List<RideResponse>> getMyRides(String driverId, Sort sort);

    /**
     * As a user, I want to get the details of a carpool ride.
     * @param rideId Ride ID
     * @return ResponseEntity<RideResponse> RideResponse DTO
     */
    ResponseEntity<RideResponse> getRide(Long rideId);
}
