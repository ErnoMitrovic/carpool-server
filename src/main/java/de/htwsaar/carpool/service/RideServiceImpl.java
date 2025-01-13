package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ApiResponseDTO;
import de.htwsaar.carpool.domain.ApiResponseStatus;
import de.htwsaar.carpool.domain.request.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.request.ride.GetRidesRequest;
import de.htwsaar.carpool.domain.response.ride.RideResponse;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Location;
import de.htwsaar.carpool.model.Ride;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.repository.RideRepository;
import de.htwsaar.carpool.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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

    private final GeometryFactory geometryFactory = new GeometryFactory(
            new PrecisionModel(), SRID
    );

    public RideServiceImpl(RideRepository rideRepository, LocationRepository locationRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }


    /**
     * Get all available rides based on the request
     * @param getRidesRequest DTO containing request details
     * @return ResponseEntity containing ApiResponseDTO
     * @throws RideNotFoundException if no rides are found
     */
    @Override
    public ResponseEntity<ApiResponseDTO<?>> getFilteredRides(GetRidesRequest getRidesRequest) throws RideNotFoundException {

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

        return ResponseEntity.ok(
                new ApiResponseDTO<>(ApiResponseStatus.SUCCESS, rides));
    }

    /**
     * Create a new ride as follows:
     * 1. Checks if the driver exists
     * 2. If the location is not found, create a new location
     * @param createRideRequest DTO containing ride details
     * @return ResponseEntity containing ApiResponseDTO
     * @throws DriverNotFoundException if the driver is not found
     */
    @Override
    public ResponseEntity<ApiResponseDTO<?>> createRide(CreateRideRequest createRideRequest)
            throws DriverNotFoundException {
        // Check if the driver exists
        CarpoolUser driver = userRepository.findById(createRideRequest.driverId()).orElseThrow(
                () -> new DriverNotFoundException("Driver not found")
        );

        Point startLocation = geometryFactory.createPoint(
                new Coordinate(createRideRequest.startLocation().getX(), createRideRequest.startLocation().getY())
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

        // Create a new ride
        Ride ride = new Ride();
        Instant departureDatetime = Instant.parse(createRideRequest.departureDatetime());
        ride.setDepartureDatetime(departureDatetime);
        ride.setStart(start);
        ride.setEnd(end);
        ride.setAvailableSeats(createRideRequest.availableSeats());
        ride.setCostPerSeat(createRideRequest.costPerSeat());
        ride.setDriver(driver);

        rideRepository.save(ride);

        RideResponse rideResponse = buildRideResponse(ride);
        log.atDebug().log("Ride created successfully");

        return ResponseEntity.ok(
                new ApiResponseDTO<>(ApiResponseStatus.SUCCESS, rideResponse));
    }

    private RideResponse buildRideResponse(Ride ride) {
        RideResponse rideDTO = new RideResponse();
        rideDTO.setId(ride.getId());
        rideDTO.setDepartureTime(ride.getDepartureDatetime());
        rideDTO.setStartLocation(ride.getStart().getPosition().toText());
        rideDTO.setEndLocation(ride.getEnd().getPosition().toText());
        rideDTO.setSeats(ride.getAvailableSeats());
        rideDTO.setPrice(ride.getCostPerSeat());
        return rideDTO;
    }
}
