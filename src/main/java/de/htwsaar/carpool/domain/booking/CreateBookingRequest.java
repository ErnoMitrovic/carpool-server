package de.htwsaar.carpool.domain.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateBookingRequest (
        @NotNull
        Long rideId
) {
}
