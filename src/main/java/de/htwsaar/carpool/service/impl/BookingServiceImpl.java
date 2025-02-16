package de.htwsaar.carpool.service.impl;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<CreateBookingResponse> createBooking(Long userId, Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(RideNotFoundException::new);

        if (ride.getAvailableSeats() <= 0) {
            throw new UnavailableSeatsException();
        }

        Optional<Booking> existingBooking = bookingRepository.findBookingByRideAndCarpoolUserId(ride, userId);
        if (existingBooking.isPresent()) {
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
}
