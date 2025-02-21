package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @param statusValue the status of the booking
     * @param pageRequest the page request for the pagination
     * @return a response with the booking details
     */
    ResponseEntity<Page<BookingResponse>> getBookings(Long userId, Long rideId, BookingStatusValue statusValue, Pageable pageRequest);

    /**
     * The standard service to update the booking status
     * @param driverId the driver that is updating the status
     * @param rideId the ride referenced
     * @param bookingId the booking referenced
     * @param status the status to update to
     * @return a response with the updated booking details
     */
    ResponseEntity<BookingResponse> updateBookingStatus(Long driverId, Long rideId, Long bookingId, BookingStatusValue status);
}
