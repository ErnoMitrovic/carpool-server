package de.htwsaar.carpool.unit;

import de.htwsaar.carpool.domain.location.PointDTO;
import de.htwsaar.carpool.domain.ride.CreateRideRequest;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.UpdateRideRequest;
import de.htwsaar.carpool.exceptions.DriverNotFoundException;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Location;
import de.htwsaar.carpool.model.Ride;
import de.htwsaar.carpool.model.RideStatus;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.repository.RideRepository;
import de.htwsaar.carpool.repository.RideStatusRepository;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.RideServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
    private final static int SRID = 4326;
    private final static GeometryFactory geometryFactory = new GeometryFactory(
        new PrecisionModel(), SRID
    );

    @Mock
    private RideRepository rideRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RideStatusRepository rideStatusRepository;
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    public void testCreateRide_Success() throws DriverNotFoundException {
        // Mock inputs
        CreateRideRequest request = new CreateRideRequest(
                Instant.now().toString(),
                3,
                10.50f,
                new PointDTO(-74.0060, 40.7128),
                new PointDTO(-118.2437, 34.0522),
                "A sample ride",
                1L
        );

        CarpoolUser driver = new CarpoolUser();
        driver.setId(1L);

        Location startLocation = new Location();
        startLocation.setId(1L);
        startLocation.setPosition(
                geometryFactory.createPoint(
                        new Coordinate(-74.0060, 40.7128)
                )
        );

        Location endLocation = new Location();
        endLocation.setId(2L);
        endLocation.setPosition(
                geometryFactory.createPoint(
                        new Coordinate(-118.2437, 34.0522)
                )
        );

        RideStatus rideStatus = new RideStatus();
        rideStatus.setName("AVAILABLE");

        // Mock repository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(locationRepository.findByPosition(any(org.locationtech.jts.geom.Point.class))).thenReturn(Optional.empty());
        when(locationRepository.save(any(Location.class))).thenReturn(startLocation).thenReturn(endLocation);
        when(rideStatusRepository.findByName("AVAILABLE")).thenReturn(Optional.of(rideStatus));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        ResponseEntity<RideResponse> response = rideService.createRide(request);

        // Verify response
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().seats());
    }

    @Test
    public void testCreateRide_DriverNotFound() {
        // Mock inputs
        CreateRideRequest request = new CreateRideRequest(
                Instant.now().toString(),
                3,
                10.50f,
                new PointDTO(-74.0060, 40.7128),
                new PointDTO(-118.2437, 34.0522),
                "A sample ride",
                2L
        );

        // Mock repository behavior
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Call the method
        try {
            rideService.createRide(request);
        } catch (DriverNotFoundException e) {
            assertEquals("Driver not found", e.getMessage());
        }
    }

    @Test
    public void testUpdateRide_Success() throws RideNotFoundException, UnauthorizedDriverException {
        Ride ride = new Ride();
        ride.setId(1L);
        CarpoolUser driver = new CarpoolUser();
        driver.setId(1L);
        driver.setName("John Doe");
        ride.setDriver(driver);

        Point start = geometryFactory.createPoint(new Coordinate(-74.0060, 40.7128));
        Point end = geometryFactory.createPoint(new Coordinate(-118.2437, 34.0522));

        UpdateRideRequest request = new UpdateRideRequest(
                 "2025-01-15T10:00:00Z",
                4,
                15.0f,
                "Updated description",
                new PointDTO(start.getX(), end.getY()),
                new PointDTO(end.getX(), end.getY()),
                1L
        );

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(locationRepository.findByPosition(any(Point.class))).thenReturn(Optional.empty());
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<RideResponse> response = rideService.updateRide(1L, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, Objects.requireNonNull(response.getBody()).seats());
    }
}
