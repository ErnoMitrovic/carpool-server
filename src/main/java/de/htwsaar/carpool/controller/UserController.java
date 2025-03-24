package de.htwsaar.carpool.controller;

import com.google.firebase.auth.FirebaseAuthException;
import de.htwsaar.carpool.domain.booking.BookingResponse;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/${api.version}/user/{userId}")
@RequiredArgsConstructor
public class UserController {
    private final BookingService bookingService;

    @Operation(summary = "Get bookings for a user")
    @GetMapping("/booking")
    @PreAuthorize("hasAuthority('USER') && #userId == authentication.name")
    @PageableAsQueryParam
    public ResponseEntity<Page<BookingResponse>> getBookings(
            @PathVariable String userId,
            BookingStatusValue statusValue,
            Pageable pageable
    ) throws FirebaseAuthException {
        return bookingService.getUserBookings(userId, statusValue, pageable);
    }

    @DeleteMapping("/booking/{bookingId}")
    @PreAuthorize("hasAuthority('USER') && #userId == authentication.name")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable String userId,
            @PathVariable Long bookingId
    ) {
        bookingService.cancelBooking(userId, bookingId);
        return ResponseEntity.noContent().build();
    }
}
