package de.htwsaar.carpool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UsersHaveRideId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -5571980444741560409L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull
    @Column(name = "ride_id", nullable = false)
    private Long rideId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersHaveRideId entity = (UsersHaveRideId) o;
        return Objects.equals(this.rideId, entity.rideId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rideId, userId);
    }

}