package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "ride")
@SequenceGenerator(name = "ride_id_seq", sequenceName = "ride_id_seq", allocationSize = 1)
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ride_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "departure_datetime", nullable = false)
    private Instant departureDatetime;

    @NotNull
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @NotNull
    @Column(name = "cost_per_seat", nullable = false)
    private Float costPerSeat;

    @NotNull
    @Column(name = "ride_description", nullable = false, length = Integer.MAX_VALUE)
    private String rideDescription;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private CarpoolUser driver;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_status_id", nullable = false)
    private RideStatus rideStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "start_id", nullable = false)
    private Location start;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "end_id", nullable = false)
    private Location end;

}