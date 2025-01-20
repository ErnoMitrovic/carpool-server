package de.htwsaar.carpool.domain.location;

import jakarta.validation.constraints.NotNull;

public record CreateLocationRequest(
        @NotNull
        Double latitude,
        @NotNull
        Double longitude
) {
}
