package de.htwsaar.carpool.domain.location;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * CreateLocationRequest DTO
 * @param name the name of the location
 * @param position the position of the location
 * @param address the address of the location
 */
@Builder
public record CreateLocationRequest(
        @NotNull String name,
        @NotNull PointDTO position,
        @NotNull String address
) {
}
