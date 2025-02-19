package de.htwsaar.carpool.seed;

import de.htwsaar.carpool.domain.ride.RideStatusValue;
import de.htwsaar.carpool.repository.RideStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideStatusSeeder {

    private final RideStatusRepository rideStatusRepository;

    /**
     * Seeds the `message_status` table with predefined values in the {@link RideStatusValue}` enum.
     */
    @PostConstruct
    public void seedRideStatuses() {
        for (RideStatusValue statusValue : RideStatusValue.values()) {
            // Check if the status already exists to avoid duplicates
            if (rideStatusRepository.findByName(statusValue.name()).isEmpty()) {
                de.htwsaar.carpool.model.RideStatus status = new de.htwsaar.carpool.model.RideStatus();
                status.setName(statusValue.name());
                rideStatusRepository.save(status);
            }
        }
    }
}

