package de.htwsaar.carpool.seed;

import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.repository.BookingStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingStatusSeeder {

    private final BookingStatusRepository bookingStatusRepository;

    /**
     * Seeds the `booking_status` table with predefined values in the {@link BookingStatusValue}` enum.
     */
    @PostConstruct
    public void seedBookingStatuses() {
        for (BookingStatusValue statusValue : BookingStatusValue.values()) {
            // Check if the status already exists to avoid duplicates
            if (bookingStatusRepository.findByName(statusValue.name()).isEmpty()) {
                de.htwsaar.carpool.model.BookingStatus status = new de.htwsaar.carpool.model.BookingStatus();
                status.setName(statusValue.name());
                bookingStatusRepository.save(status);
            }
        }
    }
}

