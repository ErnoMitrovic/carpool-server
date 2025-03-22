package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.LocationResponse;

import java.io.Serializable;

public record RideResponse(
        Long id,
        String departureDatetime,
        LocationResponse startLocation,
        LocationResponse endLocation,
        Integer seats,
        Float price,
        String status,
        String description
) implements Serializable {
}
