package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Booking;
import de.htwsaar.carpool.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Used to guarantee that the user doesn't have already booked this ride
     * @param ride the ride referenced
     * @param userId the user's id
     * @return an optional of booking
     */
    Optional<Booking> findBookingByRideAndCarpoolUserId(Ride ride, Long userId);
}
