package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import de.htwsaar.carpool.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        return bookingService.createBooking(Long.valueOf(principal.getName()), rideId);
    }

    @Operation(summary = "Returns the bookings according to the ride id")
    @GetMapping
    public ResponseEntity<BookingResponse> getBookings(Principal principal, @PathVariable Long rideId) {
        return bookingService.getBookings(Long.valueOf(principal.getName()), rideId);
    }
}
