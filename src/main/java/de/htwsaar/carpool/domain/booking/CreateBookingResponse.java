package de.htwsaar.carpool.domain.booking;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record CreateBookingResponse(
        @NotNull Long id
) implements Serializable {
}
