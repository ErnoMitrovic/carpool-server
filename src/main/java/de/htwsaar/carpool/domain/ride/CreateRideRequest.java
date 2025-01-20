package de.htwsaar.carpool.domain.ride;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.geo.Point;

public record CreateRideRequest(
        @NotNull @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z)$")
        String departureDatetime,
        @NotNull
        Integer availableSeats,
        @NotNull
        Float costPerSeat,
        @NotNull
        Point startLocation,
        @NotNull
        Point endLocation,
        @NotNull
        String rideDescription,
        @NotNull
        Long driverId // TODO: Extract from authentication
) {
}