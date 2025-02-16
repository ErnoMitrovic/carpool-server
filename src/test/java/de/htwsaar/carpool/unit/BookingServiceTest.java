package de.htwsaar.carpool.unit;

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
import de.htwsaar.carpool.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingStatusRepository bookingStatusRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Ride testRide;
    private CarpoolUser testUser;
    private BookingStatus testStatus;

    @BeforeEach
    void setUp() {
        testRide = new Ride();
        testRide.setId(1L);
        testRide.setAvailableSeats(3);

        testUser = new CarpoolUser();
        testUser.setId(1L);

        testStatus = new BookingStatus();
        testStatus.setName(BookingStatusValue.ACCEPTED.name());
    }

    @Test
    void createBooking_ShouldCreateBooking_WhenValid() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingStatusRepository.findByName(BookingStatusValue.ACCEPTED.name()))
                .thenReturn(Optional.of(testStatus));
        when(bookingRepository.findBookingByRideAndCarpoolUserId(testRide, 1L)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(10L);
            return savedBooking;
        });

        ResponseEntity<CreateBookingResponse> response = bookingService.createBooking(1L, 1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(10L, Objects.requireNonNull(response.getBody()).id());
        verify(rideRepository).save(testRide);
        assertEquals(2, testRide.getAvailableSeats()); // Seat should be reduced
    }

    @Test
    void createBooking_ShouldThrowRideNotFoundException_WhenRideDoesNotExist() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> bookingService.createBooking(1L, 1L));
    }

    @Test
    void createBooking_ShouldThrowUnavailableSeatsException_WhenNoSeatsAvailable() {
        testRide.setAvailableSeats(0);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));

        assertThrows(UnavailableSeatsException.class, () -> bookingService.createBooking(1L, 1L));
    }

    @Test
    void createBooking_ShouldThrowBookedException_WhenUserAlreadyBooked() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(bookingRepository.findBookingByRideAndCarpoolUserId(testRide, 1L))
                .thenReturn(Optional.of(new Booking()));

        assertThrows(BookedException.class, () -> bookingService.createBooking(1L, 1L));
    }

    @Test
    void createBooking_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(bookingRepository.findBookingByRideAndCarpoolUserId(testRide, 1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(1L, 1L));
    }

    @Test
    void createBooking_ShouldThrowStatusNotFound_WhenBookingStatusNotFound() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingRepository.findBookingByRideAndCarpoolUserId(testRide, 1L)).thenReturn(Optional.empty());
        when(bookingStatusRepository.findByName(BookingStatusValue.ACCEPTED.name())).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () -> bookingService.createBooking(1L, 1L));
    }
}