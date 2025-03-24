package de.htwsaar.carpool.unit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GetUsersResult;
import com.google.firebase.auth.UserRecord;
import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import de.htwsaar.carpool.domain.ride.RideStatusValue;
import de.htwsaar.carpool.exceptions.*;
import de.htwsaar.carpool.model.*;
import de.htwsaar.carpool.repository.*;
import de.htwsaar.carpool.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private UsersHaveRideRepository usersHaveRideRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Ride testRide;
    private CarpoolUser testUser;
    private BookingStatus acceptedStatus;
    private BookingStatus rejectedStatus;
    private BookingStatus pendingStatus;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = new CarpoolUser();
        testUser.setId("1L");

        RideStatus rideStatus = new RideStatus();
        rideStatus.setName(RideStatusValue.AVAILABLE.name());
        testRide = new Ride();
        testRide.setId(1L);
        testRide.setAvailableSeats(3);
        testRide.setRideStatus(rideStatus);
        testRide.setDriver(testUser);

        acceptedStatus = new BookingStatus();
        acceptedStatus.setName(BookingStatusValue.ACCEPTED.name());

        rejectedStatus = new BookingStatus();
        rejectedStatus.setName(BookingStatusValue.REJECTED.name());

        pendingStatus = new BookingStatus();
        pendingStatus.setName(BookingStatusValue.PENDING.name());

        testBooking = new Booking();
        testBooking.setId(100L);
        testBooking.setRide(testRide);
        testBooking.setCarpoolUser(testUser);
        testBooking.setBookingStatus(pendingStatus);
    }

    @Test
    void createBooking_ShouldCreateBooking_WhenValid() {
        when(rideRepository.findById(anyLong())).thenReturn(Optional.of(testRide));
        when(bookingRepository.existsByRideAndCarpoolUserId(any(Ride.class), anyString()))
                .thenReturn(false);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        when(bookingStatusRepository.findByName(BookingStatusValue.PENDING.name()))
                .thenReturn(Optional.of(pendingStatus));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(10L);
            return savedBooking;
        });

        ResponseEntity<CreateBookingResponse> response = bookingService.createBooking("1L", 1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(10L, Objects.requireNonNull(response.getBody()).id());
        verify(rideRepository).save(testRide);
        assertEquals(2, testRide.getAvailableSeats()); // Seat should be reduced
    }

    @Test
    void createBooking_ShouldThrowRideNotFoundException_WhenRideDoesNotExist() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> bookingService.createBooking("1L", 1L));
    }

    @Test
    void createBooking_ShouldThrowUnavailableSeatsException_WhenNoSeatsAvailable() {
        testRide.setAvailableSeats(0);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));

        assertThrows(UnavailableSeatsException.class, () -> bookingService.createBooking("1L", 1L));
    }

    @Test
    void createBooking_ShouldThrowBookedException_WhenUserAlreadyBooked() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(bookingRepository.existsByRideAndCarpoolUserId(testRide, "1L"))
                .thenReturn(true);

        assertThrows(BookedException.class, () -> bookingService.createBooking("1L", 1L));
    }

    @Test
    void createBooking_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(bookingRepository.existsByRideAndCarpoolUserId(testRide, "1L")).thenReturn(false);
        when(userRepository.findById("1L")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking("1L", 1L));
    }

    @Test
    void createBooking_ShouldThrowStatusNotFound_WhenBookingStatusNotFound() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(userRepository.findById("1L")).thenReturn(Optional.of(testUser));
        when(bookingRepository.existsByRideAndCarpoolUserId(testRide, "1L")).thenReturn(false);
        when(bookingStatusRepository.findByName(BookingStatusValue.PENDING.name())).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () -> bookingService.createBooking("1L", 1L));
    }

    @Test
    void getBookings_ReturnsPaginatedBookings() throws FirebaseAuthException {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> bookingPage = new PageImpl<>(List.of(testBooking), pageable, 1);

        when(rideRepository.existsByIdAndDriverId(1L, "2L")).thenReturn(true);
        when(bookingRepository.findAllByRideIdAndBookingStatusName(1L, "PENDING", pageable))
                .thenReturn(bookingPage);

        GetUsersResult result = mock(GetUsersResult.class);
        UserRecord userRecord = mock(UserRecord.class);

        when(firebaseAuth.getUsers(anyCollection())).thenReturn(result);
        when(result.getUsers()).thenReturn(Set.of(userRecord));
        when(userRecord.getUid()).thenReturn("1L");
        when(userRecord.getDisplayName()).thenReturn("Test User");

        var response = bookingService.getBookings("2L", 1L, BookingStatusValue.PENDING, pageable);

        assertNotNull(response);
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertEquals(100L, response.getBody().getContent().get(0).bookingId());
    }

    @Test
    void getBookings_ReturnsEmptyPage_WhenNoBookingsFound() throws FirebaseAuthException {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> emptyPage = Page.empty(pageable);

        GetUsersResult result = mock(GetUsersResult.class);

        when(rideRepository.existsByIdAndDriverId(anyLong(), anyString())).thenReturn(true);
        when(bookingRepository.findAllByRideIdAndBookingStatusName(1L, "PENDING", pageable))
                .thenReturn(emptyPage);
        when(firebaseAuth.getUsers(anyCollection())).thenReturn(result);
        when(result.getUsers()).thenReturn(Set.of());

        var response = bookingService.getBookings("2L", 1L, BookingStatusValue.PENDING, pageable);

        assertNotNull(response);
        assertEquals(0, Objects.requireNonNull(response.getBody()).getTotalElements());
    }

    @Test
    void getBookings_ThrowsException_WhenRideNotFound() {
        Pageable pageable = PageRequest.of(0, 5);

        when(rideRepository.existsByIdAndDriverId(anyLong(), anyString())).thenReturn(true);
        when(bookingRepository.findAllByRideIdAndBookingStatusName(999L, "PENDING", pageable))
                .thenThrow(new RideNotFoundException());

        assertThrows(RideNotFoundException.class, () ->
                bookingService.getBookings("2L", 999L, BookingStatusValue.PENDING, pageable)
        );
    }

    @Test
    void updateBookingStatus_AcceptBooking_Success() throws FirebaseAuthException {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingStatusRepository.findByName("ACCEPTED")).thenReturn(Optional.of(acceptedStatus));

        UserRecord userRecord = Mockito.mock(UserRecord.class);
        when(firebaseAuth.getUser(anyString())).thenReturn(userRecord);
        when(usersHaveRideRepository.save(any(UsersHaveRide.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<BookingResponse> response = bookingService.updateBookingStatus(
                "1L", 1L, 100L, BookingStatusValue.ACCEPTED);

        assertNotNull(response);
        assertEquals("ACCEPTED", Objects.requireNonNull(response.getBody()).bookingStatus());
        assertEquals(2, testRide.getAvailableSeats());
    }

    @Test
    void updateBookingStatus_DeclineBooking_Success() throws FirebaseAuthException {
        UserRecord userRecord = mock(UserRecord.class);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingStatusRepository.findByName(BookingStatusValue.REJECTED.name())).thenReturn(Optional.of(rejectedStatus));
        when(firebaseAuth.getUser(anyString())).thenReturn(userRecord);
        when(userRecord.getDisplayName()).thenReturn("Test User");

        ResponseEntity<BookingResponse> response = bookingService.updateBookingStatus(
                "1L", 1L, 100L, BookingStatusValue.REJECTED);

        assertNotNull(response);
        assertEquals("REJECTED", Objects.requireNonNull(response.getBody()).bookingStatus());
        assertEquals(3, testRide.getAvailableSeats());  // Seat count unchanged
    }

    @Test
    void updateBookingStatus_ThrowsException_WhenBookingNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.updateBookingStatus("1L", 10L, 999L, BookingStatusValue.ACCEPTED));
    }

    @Test
    void updateBookingStatus_ThrowsException_WhenBookingNotForRide() {
        Ride anotherRide = new Ride();
        anotherRide.setId(20L);
        testBooking.setRide(anotherRide);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        assertThrows(InvalidBookingException.class, () ->
                bookingService.updateBookingStatus("1L", 10L, 100L, BookingStatusValue.ACCEPTED));
    }

    @Test
    void updateBookingStatus_ThrowsException_WhenRideIsFull() {
        testRide.setAvailableSeats(0);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        assertThrows(UnavailableSeatsException.class, () ->
                bookingService.updateBookingStatus("1L", 1L, 100L, BookingStatusValue.ACCEPTED));
    }

    @Test
    void updateBookingStatus_ThrowsException_WhenStatusNotFound() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingStatusRepository.findByName("ACCEPTED")).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () ->
                bookingService.updateBookingStatus("1L", 1L, 100L, BookingStatusValue.ACCEPTED));
    }
}