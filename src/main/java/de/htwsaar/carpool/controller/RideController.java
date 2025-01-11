package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.ApiResponseDTO;
import de.htwsaar.carpool.domain.request.ride.CreateRideDTO;
import de.htwsaar.carpool.domain.request.ride.RideSortDTO;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    // As a user, I want to search for available carpool rides based on my destination, date, and time
    // so that I can find suitable travel options.
    @Operation(summary = "Search for available carpool rides based on destination, date, and time")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved rides",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RideSortDTO.class)
                            )
                    }
            )
    })
    @GetMapping("/")
    public ResponseEntity<ApiResponseDTO<?>> searchRides(RideSortDTO rideSortDTO) throws RideNotFoundException {
        return rideService.getFilteredRides(rideSortDTO);
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
                                    schema = @Schema(implementation = CreateRideDTO.class)
                            )
                    }
            )
    })
    @PostMapping("/")
    public ResponseEntity<ApiResponseDTO<?>> createRide(@RequestBody CreateRideDTO createRideDTO) {
        return rideService.createRide(createRideDTO);
    }
}
