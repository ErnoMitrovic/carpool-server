package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import de.htwsaar.carpool.domain.location.LocationResponse;
import de.htwsaar.carpool.domain.location.PointDTO;
import de.htwsaar.carpool.domain.ride.*;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.StatusNotFound;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Location;
import de.htwsaar.carpool.model.Ride;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.repository.RideRepository;
import de.htwsaar.carpool.repository.RideStatusRepository;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.RideService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static de.htwsaar.carpool.config.Constants.SRID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RideStatusRepository rideStatusRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(
            new PrecisionModel(), SRID
    );

    private RideResponse buildRideResponse(Ride ride) {
        Point startPoint = ride.getStart().getPosition();
        Point endPoint = ride.getEnd().getPosition();
        return new RideResponse(
                ride.getId(),
                ride.getDepartureDatetime().toString(),
                LocationResponse.builder()
                        .name(ride.getStart().getName())
                        .address(ride.getStart().getAddress())
                        .position(PointDTO.builder().x(startPoint.getX()).y(startPoint.getY()).build())
                        .build(),
                LocationResponse.builder()
                        .name(ride.getEnd().getName())
                        .address(ride.getEnd().getAddress())
                        .position(PointDTO.builder().x(endPoint.getX()).y(endPoint.getY()).build())
                        .build(),
                ride.getAvailableSeats(),
                ride.getCostPerSeat(),
                ride.getRideStatus().getName()
        );
    }

    private Point convertToPoint(PointDTO pointDTO) {
        return geometryFactory.createPoint(
                new Coordinate(pointDTO.x(), pointDTO.y())
        );
    }

    private Location getOrInsertLocation(CreateLocationRequest locationRequest) {
        Point position = convertToPoint(locationRequest.position());
        return locationRepository.findByPosition(
                position
        ).orElseGet(() -> {
            log.atInfo().log("Location not found, creating new location");
            Location location = Location.builder()
                    .position(position)
                    .name(locationRequest.name())
                    .address(locationRequest.address())
                    .build();
            return locationRepository.save(location);
        });
    }


    @Override
    public ResponseEntity<List<RideResponse>> getFilteredRides(@Valid GetRidesRequest getRidesRequest) throws RideNotFoundException {

        Point startLocation = geometryFactory.createPoint(
                new Coordinate(getRidesRequest.startLocation().x(),
                        getRidesRequest.startLocation().y())
        );

        Point endLocation = geometryFactory.createPoint(
                new Coordinate(getRidesRequest.endLocation().x(),
                        getRidesRequest.endLocation().y())
        );

        List<RideResponse> rides = rideRepository.findAvailableRides(
                startLocation,
                endLocation,
                getRidesRequest.radius(),
                Instant.parse(getRidesRequest.departureDateTime())
        ).stream().map(this::buildRideResponse).toList();

        if(rides.isEmpty()) throw new RideNotFoundException();

        log.debug("Found {} rides", rides.size());

        return ResponseEntity.ok(rides);
    }

    @Override
    @Transactional
    public ResponseEntity<RideResponse> createRide(CreateRideRequest createRideRequest, String driverId)
            throws DriverNotFoundException {
        // Check if the driver exists
        CarpoolUser driver = userRepository.findById(driverId).orElseThrow(
                () -> new DriverNotFoundException("Driver not found")
        );

        // Check if the startLocation location exists
        Location start = getOrInsertLocation(createRideRequest.startLocation());

        // Check if the endLocation location exists
        Location end = getOrInsertLocation(createRideRequest.endLocation());

        // Get available status id
        de.htwsaar.carpool.model.RideStatus rideStatus = rideStatusRepository.findByName(RideStatusValue.AVAILABLE.name())
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
    public ResponseEntity<RideResponse> updateRide(Long rideId, UpdateRideRequest updateRideRequest, String driverId)
            throws RideNotFoundException {
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

        if(updateRideRequest.startLocation() != null) {
            Location start = getOrInsertLocation(updateRideRequest.startLocation());
            ride.setStart(start);
        }

        if(updateRideRequest.endLocation() != null) {
            Location end = getOrInsertLocation(updateRideRequest.endLocation());
            ride.setEnd(end);
        }

        rideRepository.save(ride);
        RideResponse rideResponse = buildRideResponse(ride);
        return ResponseEntity.ok(rideResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> cancelRide(Long rideId, String driverId)
            throws RideNotFoundException, UnauthorizedDriverException, StatusNotFound {
        Ride ride = rideRepository.findById(rideId).orElseThrow(
                () -> new RideNotFoundException("Ride not found"));

        ride.setRideStatus(rideStatusRepository.findByName(RideStatusValue.CANCELLED.name())
                .orElseThrow(() -> new StatusNotFound("Ride status not found")));

        rideRepository.save(ride);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<RideResponse>> getMyRides(String driverId, Sort sort) {
        List<RideResponse> rides = rideRepository.findAllByDriverId(driverId, sort)
                .stream()
                .map(this::buildRideResponse)
                .toList();

        return ResponseEntity.ok(rides);
    }
}
