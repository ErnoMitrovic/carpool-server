package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.booking.CreateBookingRequest;
import de.htwsaar.carpool.domain.booking.CreateBookingResponse;
import de.htwsaar.carpool.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api/${api.version}/booking")
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Creates a booking according to the ride id")
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest bookingRequest,
            Principal principal) {
        return bookingService.createBooking(Long.valueOf(principal.getName()), bookingRequest.rideId());
    }
}
