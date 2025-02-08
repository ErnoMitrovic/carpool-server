package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {

    /**
     * Finds a BookingStatus by its name.
     *
     * @param name the name of the message status
     * @return an Optional containing the found BookingStatus or empty if not found
     */
    Optional<BookingStatus> findByName(String name);
}
