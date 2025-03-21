package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import de.htwsaar.carpool.exceptions.*;
import de.htwsaar.carpool.model.Booking;
import de.htwsaar.carpool.model.BookingStatus;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Ride;
import de.htwsaar.carpool.repository.BookingRepository;
import de.htwsaar.carpool.repository.BookingStatusRepository;
import de.htwsaar.carpool.repository.RideRepository;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<CreateBookingResponse> createBooking(String userId, Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(RideNotFoundException::new);

        if (ride.getAvailableSeats() <= 0) {
            throw new UnavailableSeatsException();
        }

        List<Booking> existingBooking = bookingRepository.findBookingByRideAndCarpoolUserId(ride, userId);
        if (!existingBooking.isEmpty()) {
            throw new BookedException(userId, rideId);
        }

        CarpoolUser user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        BookingStatus bookingStatus = bookingStatusRepository.findByName(BookingStatusValue.ACCEPTED.name())
                .orElseThrow(() -> new StatusNotFound(BookingStatusValue.ACCEPTED));

        Booking booking = Booking.builder()
                .carpoolUser(user)
                .ride(ride)
                .bookingStatus(bookingStatus)
                .build();

        ride.setAvailableSeats(ride.getAvailableSeats() - 1);
        rideRepository.save(ride);

        CreateBookingResponse bookingResponse = CreateBookingResponse
                .builder()
                .id(bookingRepository.save(booking).getId())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @Override
    public ResponseEntity<Page<BookingResponse>> getBookings(String userId,
                                                             Long rideId,
                                                             BookingStatusValue statusValue,
                                                             Pageable pageable) {
        if (rideRepository.existsByIdAndDriverId(rideId, userId))
            throw new UnauthorizedDriverException(userId, rideId);

        Page<Booking> bookings = bookingRepository.findAllByRideIdAndCarpoolUserIdAndBookingStatusName(
                rideId,
                userId,
                statusValue.name(),
                pageable);

        Page<BookingResponse> responsePage = bookings.map(booking ->
                BookingResponse.builder()
                        .bookingId(booking.getId())
                        .rideId(booking.getRide().getId())
                        .username(booking.getCarpoolUser().getId())
                        // .userRole(booking.getCarpoolUser().getRole().getName())
                        .bookingStatus(booking.getBookingStatus().getName())
                        .rideStatus(booking.getRide().getRideStatus().getName())
                        .build());

        return ResponseEntity.ok(responsePage);
    }

    @Override
    public ResponseEntity<BookingResponse> updateBookingStatus(String driverId, Long rideId, Long bookingId, BookingStatusValue status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Ride ride = booking.getRide();

        if (!ride.getId().equals(rideId)) {
            throw new InvalidBookingException();
        }

        if (status == BookingStatusValue.ACCEPTED) {
            if (ride.getAvailableSeats() == 0) {
                throw new UnavailableSeatsException();
            }
            ride.setAvailableSeats(ride.getAvailableSeats() - 1);
        }

        BookingStatus bookingStatus = bookingStatusRepository.findByName(status.name())
                .orElseThrow(() -> new StatusNotFound(status));

        booking.setBookingStatus(bookingStatus);
        bookingRepository.save(booking);
        rideRepository.save(ride);

        BookingResponse response = BookingResponse.builder()
                .bookingId(booking.getId())
                .rideId(ride.getId())
                .username(booking.getCarpoolUser().getId())
                .bookingStatus(booking.getBookingStatus().getName())
                .rideStatus(ride.getRideStatus().getName())
                .build();

        return ResponseEntity.ok(response);
    }
}
