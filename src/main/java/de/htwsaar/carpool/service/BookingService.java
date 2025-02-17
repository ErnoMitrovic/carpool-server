package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    /**
     * The standard service to create a booking based on the userId and the rideId
     * @param userId the user that made the booking
     * @param rideId the ride referenced
     * @return a response with only the id
     */
    @Transactional
    ResponseEntity<CreateBookingResponse> createBooking(Long userId, Long rideId);

    /**
     * The standard service to get the bookings based on the rideId
     * @param userId the user that made the booking
     * @param rideId the ride referenced
     * @return a response with the booking details
     */
    ResponseEntity<BookingResponse> getBookings(Long userId, Long rideId);
}
