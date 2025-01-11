package de.htwsaar.carpool.domain.request.ride;

public record CreateRideDTO(
        String departureTime,
        String destination,
        int availableSeats,
        double costPerSeat,
        String startLocation,
        String endLocation
) {
}