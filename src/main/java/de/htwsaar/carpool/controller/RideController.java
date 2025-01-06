package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.dto.ApiResponseDTO;
import de.htwsaar.carpool.dto.ride.RideDTO;
import de.htwsaar.carpool.dto.ride.RideSortDTO;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/search")
    public ResponseEntity<ApiResponseDTO<?>> searchRides(@RequestBody RideSortDTO rideSortDTO) throws RideNotFoundException {
        return rideService.getFilteredRides(rideSortDTO);
    }
}
