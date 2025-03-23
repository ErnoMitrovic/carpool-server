package de.htwsaar.carpool.controller;

import com.google.firebase.auth.FirebaseAuthException;
import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import de.htwsaar.carpool.domain.booking.SetStatusRequest;
import de.htwsaar.carpool.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/${api.version}/ride/{rideId}/booking")
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Creates a booking according to the ride id")
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
            Principal principal, @PathVariable Long rideId) {
        return bookingService.createBooking(principal.getName(), rideId);
    }

    @Operation(summary = "Returns the bookings according to the ride id")
    @GetMapping
    @PreAuthorize("hasAuthority('DRIVER')")
    public ResponseEntity<Page<BookingResponse>> getBookings(@RequestParam BookingStatusValue statusValue,
                                                             Authentication principal,
                                                             @PathVariable Long rideId,
                                                             @RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) throws FirebaseAuthException {

        return bookingService.getBookings(
                principal.getName(),
                rideId,
                statusValue,
                PageRequest.of(page, size));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            Principal principal,
            @PathVariable Long rideId,
            @PathVariable Long bookingId,
            @RequestBody SetStatusRequest statusRequest) throws FirebaseAuthException {

        return bookingService.updateBookingStatus(principal.getName(), rideId, bookingId, statusRequest.status());
    }
}
