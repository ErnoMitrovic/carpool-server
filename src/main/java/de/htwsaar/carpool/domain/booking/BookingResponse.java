package de.htwsaar.carpool.domain.booking;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record BookingResponse (
        Long bookingId,
        Long rideId,
        String username,
        String bookingStatus,
        String rideStatus
) implements Serializable {
}
