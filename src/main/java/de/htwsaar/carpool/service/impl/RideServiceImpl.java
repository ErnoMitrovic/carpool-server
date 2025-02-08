package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.ride.*;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.StatusNotFound;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Location;
import de.htwsaar.carpool.model.Ride;
import de.htwsaar.carpool.model.RideStatus;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.repository.RideRepository;
import de.htwsaar.carpool.repository.RideStatusRepository;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.RideService;
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
import java.util.Objects;

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

    private Location getOrInsertLocation(Point position) {
        return locationRepository.findByPosition(
                position
        ).orElseGet(() -> {
            log.atInfo().log("Location not found, creating new location");
            Location location = new Location();
            location.setPosition(position);
            return locationRepository.save(location);
        });
    }


    @Override
    public ResponseEntity<List<RideResponse>> getFilteredRides(GetRidesRequest getRidesRequest) throws RideNotFoundException {

        List<RideResponse> rides = rideRepository.findAvailableRides(
                getRidesRequest.getStartLocation().x(),
                getRidesRequest.getStartLocation().y(),
                getRidesRequest.getEndLocation().x(),
                getRidesRequest.getEndLocation().y(),
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

    @Override
    @Transactional
    public ResponseEntity<RideResponse> createRide(CreateRideRequest createRideRequest, Long driverId)
            throws DriverNotFoundException {
        // Check if the driver exists
        CarpoolUser driver = userRepository.findById(driverId).orElseThrow(
                () -> new DriverNotFoundException("Driver not found")
        );

        Point startLocation = geometryFactory.createPoint(
                new Coordinate(createRideRequest.startLocation().x(),
                        createRideRequest.startLocation().y())
        );

        Point endLocation = geometryFactory.createPoint(
                new Coordinate(createRideRequest.endLocation().x(),
                        createRideRequest.endLocation().y())
        );

        // Check if the start location exists
        Location start = getOrInsertLocation(startLocation);

        // Check if the end location exists
        Location end = getOrInsertLocation(endLocation);

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

    @Override
    @Transactional
    public ResponseEntity<RideResponse> updateRide(Long rideId, UpdateRideRequest updateRideRequest, Long driverId)
            throws RideNotFoundException {
        // TODO: Implement authorization check and obtain driver id from security context
        Ride ride = rideRepository.findById(rideId).orElseThrow(
                () -> new RideNotFoundException("Ride not found"));

        if(!Objects.equals(ride.getDriver().getId(), driverId)) {
            throw new UnauthorizedDriverException("Driver is not authorized to update this ride");
        }

        // Update ride if it is not null
        if(updateRideRequest.departureDateTime() != null) {
            ride.setDepartureDatetime(Instant.parse(updateRideRequest.departureDateTime()));
        }

        if(updateRideRequest.availableSeats() != null) {
            ride.setAvailableSeats(updateRideRequest.availableSeats());
        }

        if(updateRideRequest.costPerSeat() != null) {
            ride.setCostPerSeat(updateRideRequest.costPerSeat());
        }

        if(updateRideRequest.rideDescription() != null) {
            ride.setRideDescription(updateRideRequest.rideDescription());
        }

        if(updateRideRequest.start() != null) {
            Point startLocation = geometryFactory.createPoint(
                    new Coordinate(updateRideRequest.start().x(),
                            updateRideRequest.start().y())
            );
            Location start = getOrInsertLocation(startLocation);
            ride.setStart(start);
        }

        if(updateRideRequest.end() != null) {
            Point endLocation = geometryFactory.createPoint(
                    new Coordinate(updateRideRequest.end().x(),
                            updateRideRequest.end().y())
            );
            Location end = getOrInsertLocation(endLocation);
            ride.setEnd(end);
        }

        rideRepository.save(ride);
        RideResponse rideResponse = buildRideResponse(ride);
        return ResponseEntity.ok(rideResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> cancelRide(Long rideId, Long driverId)
            throws RideNotFoundException, UnauthorizedDriverException, StatusNotFound {
        Ride ride = rideRepository.findById(rideId).orElseThrow(
                () -> new RideNotFoundException("Ride not found"));

        if(!Objects.equals(ride.getDriver().getId(), driverId)) {
            throw new UnauthorizedDriverException("Driver is not authorized to cancel this ride");
        }

        ride.setRideStatus(rideStatusRepository.findByName(RideStatusValue.CANCELLED.name())
                .orElseThrow(() -> new StatusNotFound("Ride status not found")));

        rideRepository.save(ride);
        return ResponseEntity.noContent().build();
    }
}
