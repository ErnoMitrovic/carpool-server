package de.htwsaar.carpool.service.impl;

import com.google.firebase.auth.*;
import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import de.htwsaar.carpool.exceptions.*;
import de.htwsaar.carpool.model.*;
import de.htwsaar.carpool.repository.*;
import de.htwsaar.carpool.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final UserRepository userRepository;
    private final FirebaseAuth firebaseAuth;
    private final UsersHaveRideRepository usersHaveRideRepository;

    @Override
    @Transactional
    public ResponseEntity<CreateBookingResponse> createBooking(String userId, Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(RideNotFoundException::new);

        if (ride.getAvailableSeats() <= 0) {
            throw new UnavailableSeatsException();
        }

        if (bookingRepository.existsByRideAndCarpoolUserId(ride, userId)) {
            throw new BookedException(userId, rideId);
        }

        CarpoolUser user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        BookingStatus bookingStatus = bookingStatusRepository.findByName(BookingStatusValue.PENDING.name())
                .orElseThrow(() -> new StatusNotFound(BookingStatusValue.PENDING));

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
                                                             Pageable pageable) throws FirebaseAuthException {
        if (!rideRepository.existsByIdAndDriverId(rideId, userId))
            throw new UnauthorizedDriverException(userId, rideId);

        Page<Booking> bookings = bookingRepository.findAllByRideIdAndBookingStatusName(
                rideId,
                statusValue.name(),
                pageable);

        List<UserIdentifier> userIds = bookings.map(booking -> (UserIdentifier) new UidIdentifier(booking.getCarpoolUser().getId()))
                .get().toList();

        var userNamesMap = firebaseAuth.getUsers(userIds)
                .getUsers()
                .stream()
                .collect(Collectors.toMap(
                        UserRecord::getUid,
                        UserRecord::getDisplayName));

        Page<BookingResponse> responsePage = bookings.map(booking ->
                BookingResponse.builder()
                        .bookingId(booking.getId())
                        .rideId(booking.getRide().getId())
                        .username(userNamesMap.get(booking.getCarpoolUser().getId()))
                        .bookingStatus(booking.getBookingStatus().getName())
                        .rideStatus(booking.getRide().getRideStatus().getName())
                        .build());

        return ResponseEntity.ok(responsePage);
    }

    @Override
    public ResponseEntity<BookingResponse> updateBookingStatus(String driverId, Long rideId, Long bookingId, BookingStatusValue status) throws FirebaseAuthException {
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

            UsersHaveRideId usersHaveRideId = new UsersHaveRideId();
            usersHaveRideId.setUserId(booking.getCarpoolUser().getId());
            usersHaveRideId.setRideId(rideId);
            UsersHaveRide usersHaveRide = UsersHaveRide.builder()
                    .id(usersHaveRideId)
                    .carpoolUser(booking.getCarpoolUser())
                    .ride(ride)
                    .build();
            usersHaveRideRepository.save(usersHaveRide);
        }

        BookingStatus bookingStatus = bookingStatusRepository.findByName(status.name())
                .orElseThrow(() -> new StatusNotFound(status));

        booking.setBookingStatus(bookingStatus);
        bookingRepository.save(booking);
        rideRepository.save(ride);

        String username = firebaseAuth.getUser(booking.getCarpoolUser().getId()).getDisplayName();

        BookingResponse response = BookingResponse.builder()
                .bookingId(booking.getId())
                .rideId(ride.getId())
                .username(username)
                .bookingStatus(booking.getBookingStatus().getName())
                .rideStatus(ride.getRideStatus().getName())
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Page<BookingResponse>> getUserBookings(String userId,
                                                                 BookingStatusValue statusValue,
                                                                 Pageable pageable)
            throws FirebaseAuthException {
        Page<Booking> bookings = bookingRepository.findAllByCarpoolUserIdAndBookingStatusName(
                userId, statusValue.name(), pageable);

        var userIds = bookings
                .map(Booking::getCarpoolUser)
                .map(CarpoolUser::getId)
                .map(uid -> (UserIdentifier) new UidIdentifier(uid)).toList();

        var userNamesMap = firebaseAuth.getUsers(userIds)
                .getUsers()
                .stream()
                .collect(Collectors.toMap(
                        UserRecord::getUid,
                        UserRecord::getDisplayName));

        return ResponseEntity.ok(bookings
                .map(booking -> BookingResponse.builder()
                        .bookingId(booking.getId())
                        .rideId(booking.getRide().getId())
                        .username(userNamesMap.get(booking.getCarpoolUser().getId()))
                        .bookingStatus(booking.getBookingStatus().getName())
                        .rideStatus(booking.getRide().getRideStatus().getName())
                        .build()));
    }

    @Override
    public void cancelBooking(String userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getCarpoolUser().getId().equals(userId)) {
            throw new UnauthorizedUserException(userId, bookingId);
        }

        if (booking.getBookingStatus().getName().equals(BookingStatusValue.ACCEPTED.name())) {
            Ride ride = booking.getRide();
            ride.setAvailableSeats(ride.getAvailableSeats() + 1);
            rideRepository.save(ride);

            UsersHaveRideId usersHaveRideId = new UsersHaveRideId();
            usersHaveRideId.setUserId(booking.getCarpoolUser().getId());
            usersHaveRideId.setRideId(ride.getId());
            usersHaveRideRepository.deleteById(usersHaveRideId);
        }

        booking.setBookingStatus(bookingStatusRepository.findByName(BookingStatusValue.CANCELLED.name())
                .orElseThrow(() -> new StatusNotFound(BookingStatusValue.CANCELLED)));
        bookingRepository.save(booking);
    }
}
