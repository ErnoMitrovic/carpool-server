package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.location.PointDTO;
import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.UpdateRideRequest;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import de.htwsaar.carpool.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/${api.version}/ride")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    // As a user, I want to search for available carpool rides based on my destination, date, and time
    // so that I can find suitable travel options.
    @Operation(summary = "Search for available carpool rides based on destination, date, and time")
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<RideResponse>> searchRides(
            @RequestParam(defaultValue = "500") Double radius,
            @RequestParam Double userLat,
            @RequestParam Double userLng,
            @RequestParam Double destLat,
            @RequestParam Double destLng,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            String departureDatetime,
            Pageable page
    ) {
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
                .departureDateTime(departureDatetime)
                .radius(radius)
                .build();
        return rideService.getFilteredRides(getRidesRequest, page);
    }

    @Operation(summary = "Get all carpool rides created by the user with pagination starting on zero")
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAuthority('DRIVER') && #driverId == authentication.name")
    public ResponseEntity<List<RideResponse>> getMyRides(@PathVariable String driverId, @SortDefault("departureDatetime") Sort sort) {
        return rideService.getMyRides(driverId, sort);
    }

    @Operation(summary = "Create a carpool ride")
    @PostMapping
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest createRideRequest, Principal principal) {
        return rideService.createRide(createRideRequest, principal.getName());
    }

    @Operation(summary = "Update a carpool ride")
    @PutMapping("/{rideId}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable Long rideId,
                                                   @Valid @RequestBody UpdateRideRequest updateRideRequest,
                                                   Principal principal)
            throws RideNotFoundException {
        return rideService.updateRide(rideId, updateRideRequest, principal.getName());
    }

    @Operation(summary = "Delete a carpool ride by updating its status to cancelled")
    @DeleteMapping("/{rideId}")
    public ResponseEntity<Void> cancelRide(@PathVariable Long rideId, Principal driverId)
            throws RideNotFoundException, UnauthorizedDriverException {
        return rideService.cancelRide(rideId, driverId.getName());
    }

    @Operation(summary = "Get a carpool ride by its id")
    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(@PathVariable Long rideId) throws RideNotFoundException {
        return rideService.getRide(rideId);
    }
}
