package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideStatusRepository extends JpaRepository<RideStatus, Long> {

    /**
     * Finds a RideStatus by its name.
     *
     * @param name the name of the message status
     * @return an Optional containing the found RideStatus or empty if not found
     */
    Optional<RideStatus> findByName(String name);

}
