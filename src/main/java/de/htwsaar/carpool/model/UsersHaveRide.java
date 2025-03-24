package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "users_have_rides")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersHaveRide {
    @EmbeddedId
    private UsersHaveRideId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private CarpoolUser carpoolUser;

    @MapsId("rideId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

}