package de.htwsaar.carpool.domain.ride;

import java.io.Serializable;

public record RideResponse(
        Long id,
        String departureTime,
        String startLocation,
        String endLocation,
        Integer seats,
        Float price
) implements Serializable {
}
