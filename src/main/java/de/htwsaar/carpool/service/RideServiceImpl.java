package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.RideStatusValue;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Location;
import de.htwsaar.carpool.model.Ride;
import de.htwsaar.carpool.model.RideStatus;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.repository.RideRepository;
import de.htwsaar.carpool.repository.RideStatusRepository;
import de.htwsaar.carpool.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static de.htwsaar.carpool.config.Constants.SRID;

@Component
@Slf4j
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RideStatusRepository rideStatusRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(
            new PrecisionModel(), SRID
    );

    public RideServiceImpl(RideRepository rideRepository,
                           LocationRepository locationRepository,
                           UserRepository userRepository,
                           RideStatusRepository rideStatusRepository) {
        this.rideRepository = rideRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.rideStatusRepository = rideStatusRepository;
    }


    /**
     * Get all available rides based on the request
     * @param getRidesRequest DTO containing request details
     * @return ResponseEntity containing list of RideResponse DTOs
     * @throws RideNotFoundException if no rides are found
     */
    @Override
    public ResponseEntity<List<RideResponse>> getFilteredRides(GetRidesRequest getRidesRequest) throws RideNotFoundException {

        List<RideResponse> rides = rideRepository.findAvailableRides(
                getRidesRequest.getStartLocation().getX(),
                getRidesRequest.getStartLocation().getY(),
                getRidesRequest.getEndLocation().getX(),
                getRidesRequest.getEndLocation().getY(),
                getRidesRequest.getRadius(),
                getRidesRequest.getSeats(),
                getRidesRequest.getDepartureTime()
        ).stream().map(this::buildRideResponse).toList();

        if(rides.isEmpty()) {
            throw new RideNotFoundException("No rides found");
        }

        log.atDebug().log("Found {} rides", rides.size());

        return ResponseEntity.ok(rides);
    }

    /**
     * Create a new ride as follows:
     * 1. Checks if the driver exists
     * 2. If the location is not found, create a new location
     * 3. As default, the status of the ride is set to AVAILABLE
     * @param createRideRequest DTO containing ride details
     * @return ResponseEntity containing RideResponse DTO
     * @throws DriverNotFoundException if the driver is not found
     */
    @Override
    @Transactional
    public ResponseEntity<RideResponse> createRide(CreateRideRequest createRideRequest)
            throws DriverNotFoundException {
        // Check if the driver exists
        CarpoolUser driver = userRepository.findById(createRideRequest.driverId()).orElseThrow(
                () -> new DriverNotFoundException("Driver not found")
        );

        Point startLocation = geometryFactory.createPoint(
                new Coordinate(createRideRequest.startLocation().getX(),
                        createRideRequest.startLocation().getY())
        );

        Point endLocation = geometryFactory.createPoint(
                new Coordinate(createRideRequest.endLocation().getX(),
                        createRideRequest.endLocation().getY())
        );

        // Check if the start location exists
        Location start =
                locationRepository.findByPosition(
                        startLocation
                ).orElseGet(() -> {
                    log.atInfo().log("Start location not found, creating new location");
                    Location location = new Location();
                    location.setPosition(startLocation);
                    return locationRepository.save(location);
                });

        // Check if the end location exists
        Location end =
                locationRepository.findByPosition(
                        endLocation
                ).orElseGet(() -> {
                    log.atInfo().log("End location not found, creating new location");
                    Location location = new Location();
                    location.setPosition(endLocation);
                    return locationRepository.save(location);
                });

        // Get available status id
        RideStatus rideStatus = rideStatusRepository.findByName(RideStatusValue.AVAILABLE.name())
                .orElseThrow(() -> new RuntimeException("Ride status not found"));

        // Create a new ride
        Ride ride = new Ride();
        Instant departureDatetime = Instant.parse(createRideRequest.departureDatetime());
        ride.setDepartureDatetime(departureDatetime);
        ride.setStart(start);
        ride.setEnd(end);
        ride.setAvailableSeats(createRideRequest.availableSeats());
        ride.setCostPerSeat(createRideRequest.costPerSeat());
        ride.setDriver(driver);
        ride.setRideDescription(createRideRequest.rideDescription());
        ride.setRideStatus(rideStatus);

        rideRepository.save(ride);

        RideResponse rideResponse = buildRideResponse(ride);
        log.atDebug().log("Ride created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(rideResponse);
    }

    private RideResponse buildRideResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getDepartureDatetime().toString(),
                ride.getStart().getPosition().toText(),
                ride.getEnd().getPosition().toText(),
                ride.getAvailableSeats(),
                ride.getCostPerSeat()
        );
    }
}
