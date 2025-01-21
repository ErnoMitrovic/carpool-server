package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.PointDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static de.htwsaar.carpool.config.Constants.DATE_TIME_FORMAT;
import static de.htwsaar.carpool.config.Constants.DATE_TIME_REGEX;

public record CreateRideRequest(
        @NotNull @Pattern(regexp = DATE_TIME_REGEX, message = "Datetime format should be " + DATE_TIME_FORMAT)
        String departureDatetime,
        @NotNull
        Integer availableSeats,
        @NotNull
        Float costPerSeat,
        PointDTO startLocation,
        @NotNull
        PointDTO endLocation,
        @NotNull
        String rideDescription,
        @NotNull
        Long driverId // TODO: Extract from authentication
) {
}