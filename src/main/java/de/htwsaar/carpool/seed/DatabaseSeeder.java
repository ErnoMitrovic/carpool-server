package de.htwsaar.carpool.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final MessageStatusSeeder messageStatusSeeder;
    private final BookingStatusSeeder bookingStatusSeeder;
    private final RideStatusSeeder rideStatusSeeder;
    private final UserRoleSeeder userRoleSeeder;

    @Override
    public void run(String... args) {
        messageStatusSeeder.seedMessageStatuses();
        bookingStatusSeeder.seedBookingStatuses();
        rideStatusSeeder.seedRideStatuses();
        userRoleSeeder.seedUserRoles();
    }

}
