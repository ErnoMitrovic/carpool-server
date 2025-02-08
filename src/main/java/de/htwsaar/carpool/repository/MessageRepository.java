package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.ride.id = :rideId " +
            "AND ((m.sender.id = :senderId AND m.receiver.id = :receiverId) " +
            "OR (m.sender.id = :receiverId AND m.receiver.id = :senderId)) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversationByRideAndUsers(
            @Param("rideId") Long rideId,
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId

    );

}
