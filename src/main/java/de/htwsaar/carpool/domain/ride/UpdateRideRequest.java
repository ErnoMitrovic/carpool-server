package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.PointDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

import static de.htwsaar.carpool.config.Constants.DATE_TIME_FORMAT;
import static de.htwsaar.carpool.config.Constants.DATE_TIME_REGEX;

/**
 * UpdateRideRequest is a data class that represents the request body for updating a ride.
 *
 * @param departureDateTime The departure date and time of the ride
 * @param availableSeats    The number of available seats in the ride
 * @param costPerSeat       The cost per seat in the ride
 * @param rideDescription   The description of the ride
 * @param start             The starting point of the ride
 * @param end               The destination point of the ride
 */
public record UpdateRideRequest(
        @Pattern(regexp = DATE_TIME_REGEX, message = "Datetime format should be " + DATE_TIME_FORMAT)
        String departureDateTime,
        @Min(value = 0, message = "Available seats should be greater than or equal to 0")
        Integer availableSeats,
        @Min(value = 0, message = "Cost per seat should be greater than or equal to 0")
        Float costPerSeat,
        @NotBlank
        String rideDescription,
        PointDTO start,
        PointDTO end
) implements Serializable {
}
