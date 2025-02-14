package de.htwsaar.carpool.domain;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String message
) {
}
