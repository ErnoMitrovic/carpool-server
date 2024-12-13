package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ride")
public class Ride {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @NotNull
    @Column(name = "cost_per_seat", nullable = false)
    private Float costPerSeat;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(name = "ride_description", nullable = false)
    private String rideDescription;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_status_id", nullable = false)
    private RideStatus rideStatus;

    @NotNull
    @Column(name = "start_location", nullable = false, columnDefinition = "point not null")
    private Point startLocation;

    @NotNull
    @Column(name = "end_location", nullable = false, columnDefinition = "point not null")
    private Point endLocation;
}