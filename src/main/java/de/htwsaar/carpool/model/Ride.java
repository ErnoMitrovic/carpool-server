package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ride")
public class Ride {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "start_location", nullable = false)
    private Long startLocation;

    @Column(name = "end_location", nullable = false)
    private Long endLocation;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "cost_per_seat", nullable = false)
    private Float costPerSeat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_status_id", nullable = false)
    private RideStatus rideStatus;

}