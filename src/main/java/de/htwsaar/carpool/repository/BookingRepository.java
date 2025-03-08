package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Booking;
import de.htwsaar.carpool.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Used to guarantee that the user doesn't have already booked this ride
     * @param ride the ride referenced
     * @param userId the user's id
     * @return an optional of booking
     */
    List<Booking> findBookingByRideAndCarpoolUserId(Ride ride, String userId);

    /**
     * Used to get all the bookings of a ride
     * @param rideId the ride referenced
     * @param userId the user's id
     * @param statusName the status of the booking
     * @return a list of bookings
     */
    Page<Booking> findAllByRideIdAndCarpoolUserIdAndBookingStatusName(Long rideId, String userId, String statusName, Pageable pageable);
}
