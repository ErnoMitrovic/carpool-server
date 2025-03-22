package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * CreateRideRequest DTO
 * @param departureDatetime the departure datetime of the ride
 * @param availableSeats the number of available seats
 * @param costPerSeat the cost per seat
 * @param startLocation the startLocation location
 * @param endLocation the endLocation location
 * @param rideDescription the ride description
 */
@Builder
public record CreateRideRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        String departureDatetime,
        @NotNull
        Integer availableSeats,
        @NotNull
        Float costPerSeat,
        CreateLocationRequest startLocation,
        @NotNull
        CreateLocationRequest endLocation,
        @NotNull
        String rideDescription
) {
}