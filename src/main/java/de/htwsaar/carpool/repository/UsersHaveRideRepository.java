package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.UsersHaveRide;
import de.htwsaar.carpool.model.UsersHaveRideId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersHaveRideRepository extends JpaRepository<UsersHaveRide, UsersHaveRideId> {
}
