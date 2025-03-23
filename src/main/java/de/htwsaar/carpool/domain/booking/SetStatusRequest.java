package de.htwsaar.carpool.domain.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * SetStatusRequest is a data class that represents the request body for setting the status of a booking.
 *
 * @param status The status to set the booking to
 */
@Builder
public record SetStatusRequest(@NotNull BookingStatusValue status) {
}
