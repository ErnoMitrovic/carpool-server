package de.htwsaar.carpool.domain.location;

import lombok.Builder;

/**
 * LocationResponse is a data class that represents the response body for a location.
 * @param position The position of the location
 * @param name The name of the location
 * @param address The address of the location
 */
@Builder
public record LocationResponse(
        PointDTO position,
        String name,
        String address
) {
}
