package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users_have_rides", schema = "carpool", indexes = {
        @Index(name = "users_have_rides_fk1_idx", columnList = "user_id")
})
public class UsersHaveRide {
    @EmbeddedId
    private UsersHaveRideId id;

    @MapsId("rideId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}