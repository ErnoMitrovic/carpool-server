package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideStatusRepository extends JpaRepository<RideStatus, Long> {
    Optional<RideStatus> findByName(String name);
}
