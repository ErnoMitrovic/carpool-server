package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import de.htwsaar.carpool.domain.location.LocationResponse;
import de.htwsaar.carpool.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Save location to the database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully saved location",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateLocationRequest.class)
                            )
                    }
            )
    })
    @PostMapping("/")
    public ResponseEntity<LocationResponse> saveLocation(@Valid @RequestBody CreateLocationRequest locationRequest) {
        return locationService.saveLocation(locationRequest);
    }
}
