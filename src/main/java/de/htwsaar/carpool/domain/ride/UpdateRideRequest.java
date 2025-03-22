package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * UpdateRideRequest is a data class that represents the request body for updating a ride.
 *
 * @param departureDatetime The departure date and time of the ride
 * @param availableSeats    The number of available seats in the ride
 * @param costPerSeat       The cost per seat in the ride
 * @param rideDescription   The description of the ride
 * @param startLocation             The starting point of the ride
 * @param endLocation               The destination point of the ride
 */
public record UpdateRideRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        String departureDatetime,
        @Min(value = 0, message = "Available seats should be greater than or equal to 0")
        Integer availableSeats,
        @Min(value = 0, message = "Cost per seat should be greater than or equal to 0")
        Float costPerSeat,
        @NotBlank
        String rideDescription,
        CreateLocationRequest startLocation,
        CreateLocationRequest endLocation
) implements Serializable {
}
