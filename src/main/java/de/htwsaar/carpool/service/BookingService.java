package de.htwsaar.carpool.service;

import com.google.firebase.auth.FirebaseAuthException;
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
     *
     * @param userId the user that made the booking
     * @param rideId the ride referenced
     * @return a response with only the id
     */
    @Transactional
    ResponseEntity<CreateBookingResponse> createBooking(String userId, Long rideId);

    /**
     * The standard service to get the bookings based on the rideId
     *
     * @param userId      the user that made the booking
     * @param rideId      the ride referenced
     * @param statusValue the status of the booking
     * @param pageRequest the page request for the pagination
     * @return a response with the booking details
     */
    ResponseEntity<Page<BookingResponse>> getBookings(String userId, Long rideId, BookingStatusValue statusValue, Pageable pageRequest) throws FirebaseAuthException;

    /**
     * The standard service to update the booking status
     *
     * @param driverId  the driver that is updating the status
     * @param rideId    the ride referenced
     * @param bookingId the booking referenced
     * @param status    the status to update to
     * @return a response with the updated booking details
     */
    ResponseEntity<BookingResponse> updateBookingStatus(String driverId, Long rideId, Long bookingId, BookingStatusValue status) throws FirebaseAuthException;

    /**
     * The standard service to get the bookings based on the userId
     *
     * @param userId      the user that made the booking
     * @param statusValue the status of the booking
     * @param pageable    the page request for the pagination
     * @return a response with the booking details
     */
    ResponseEntity<Page<BookingResponse>> getUserBookings(String userId, BookingStatusValue statusValue, Pageable pageable) throws FirebaseAuthException;

    /**
     * The standard service to cancel a booking
     *
     * @param userId    the user that made the booking
     * @param bookingId the booking referenced
     */
    void cancelBooking(String userId, Long bookingId);
}
