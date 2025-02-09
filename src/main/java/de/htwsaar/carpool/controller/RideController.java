package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.location.PointDTO;
import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.UpdateRideRequest;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import de.htwsaar.carpool.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/${api.version}/ride")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    // As a user, I want to search for available carpool rides based on my destination, date, and time
    // so that I can find suitable travel options.
    @Operation(summary = "Search for available carpool rides based on destination, date, and time")
    @GetMapping("")
    public ResponseEntity<List<RideResponse>> searchRides(
            @RequestParam Double userLat,
            @RequestParam Double userLng,
            @RequestParam Double destLat,
            @RequestParam Double destLng,
            @RequestParam(required = false, defaultValue = "10") double radius,
            @RequestParam String departureDateTime,
            @RequestParam Integer seats
    ) throws RideNotFoundException {
        GetRidesRequest getRidesRequest = GetRidesRequest
                .builder()
                .startLocation(PointDTO
                        .builder()
                        .x(userLng)
                        .y(userLat)
                        .build())
                .endLocation(PointDTO
                        .builder()
                        .x(destLng)
                        .y(destLat)
                        .build())
                .seats(seats)
                .departureDateTime(departureDateTime)
                .radius(radius)
                .build();
        return rideService.getFilteredRides(getRidesRequest);
    }

    // As a driver, I want to create a carpool ride with details like departure time, destination, available seats,
    // and pick-up points so that other users can find and join my ride.
    @Operation(summary = "Create a carpool ride")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created ride",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateRideRequest.class)
                            )
                    }
            )
    })
    @PostMapping("/")
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest createRideRequest, Principal principal)
            throws DriverNotFoundException {
        return rideService.createRide(createRideRequest, Long.valueOf(principal.getName()));
    }

    @Operation(summary = "Update a carpool ride")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated ride",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateRideRequest.class)
                            )
                    }
            )
    })
    @PutMapping("/{rideId}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable Long rideId,
                                                   @Valid @RequestBody UpdateRideRequest updateRideRequest,
                                                   Principal principal)
            throws RideNotFoundException {
        return rideService.updateRide(rideId, updateRideRequest, Long.valueOf(principal.getName()));
    }

    @Operation(summary = "Delete a carpool ride by updating its status to cancelled")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted ride",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class)
                            )
                    }
            )
    })
    @DeleteMapping("/{rideId}/")
    public ResponseEntity<Void> cancelRide(@PathVariable Long rideId, Principal driverId)
            throws RideNotFoundException, UnauthorizedDriverException {
        return rideService.cancelRide(rideId, Long.valueOf(driverId.getName()));
    }
}
