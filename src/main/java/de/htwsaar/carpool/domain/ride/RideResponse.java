package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.PointDTO;

import java.io.Serializable;

public record RideResponse(
        Long id,
        String departureTime,
        PointDTO startLocation,
        PointDTO endLocation,
        Integer seats,
        Float price
) implements Serializable {
}
