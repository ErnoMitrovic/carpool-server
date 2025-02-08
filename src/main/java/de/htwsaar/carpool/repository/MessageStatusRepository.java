package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {

    /**
     * Finds a MessageStatus by its name.
     *
     * @param name the name of the message status
     * @return an Optional containing the found MessageStatus or empty if not found
     */
    Optional<MessageStatus> findByName(String name);

}
