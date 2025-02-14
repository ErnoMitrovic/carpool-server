package de.htwsaar.carpool.integration;

import de.htwsaar.carpool.config.TestSecurityConfig;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.model.*;
import de.htwsaar.carpool.repository.*;
import de.htwsaar.carpool.service.JwtService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import redis.embedded.RedisServer;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static de.htwsaar.carpool.config.Constants.SRID;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TestSecurityConfig.class)
@ActiveProfiles(value = "test")
public class RideControllerTest {

    @LocalServerPort
    private int port;

    @Value("${api.version}")
    private String apiVersion;

    private String token;

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RideStatusRepository rideStatusRepository;
    @Autowired
    private JwtService jwtService;

    private final GeometryFactory geometryFactory = new GeometryFactory(
            new PrecisionModel(), SRID
    );

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String getBaseUrl() {
        return String.format("http://localhost:%d/api/%s", port, apiVersion);
    }

    private static RedisServer embeddedRedis;

    @BeforeAll
    static void enableEmbeddedRedis() throws IOException {
        embeddedRedis = RedisServer.newRedisServer().build();
        embeddedRedis.start();
    }

    @AfterAll
    static void stopEmbeddedRedis() throws IOException {
        if (embeddedRedis != null) {
            embeddedRedis.stop();
        }
    }

    @BeforeAll
    static void enableH2GIS(@Autowired DataSource dataSource) throws SQLException{
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR \"org.h2gis.functions.factory.H2GISFunctions.load\";");
            stmt.execute("CALL H2GIS_SPATIAL();");
        }
    }

    @BeforeEach
    public void setup() {
        rideStatusRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        locationRepository.deleteAll();
        rideRepository.deleteAll();

        RideStatus available = new RideStatus();
        available.setName("AVAILABLE");
        rideStatusRepository.save(available);

        UserRole driverRole = new UserRole();
        driverRole.setName("DRIVER");
        roleRepository.save(driverRole);

        CarpoolUser driver = new CarpoolUser();
        driver.setPhone(111);
        driver.setName("Joe Doe");
        driver.setPassword(passwordEncoder.encode("raw"));
        driver.setRole(driverRole);
        driver.setEmail("email@email.com");
        driver.setUniversityId(1L);

        userRepository.save(driver);

        // Insert a test ride
        Location startLocation = new Location();
        startLocation.setPosition(geometryFactory.createPoint(new Coordinate(9.1829, 48.7758)));
        locationRepository.save(startLocation);

        Location endLocation = new Location();
        endLocation.setPosition(geometryFactory.createPoint(new Coordinate(9.1900, 48.7800)));
        locationRepository.save(endLocation);

        Ride ride = new Ride();
        ride.setStart(startLocation);
        ride.setEnd(endLocation);
        ride.setAvailableSeats(3);
        ride.setDriver(driver);
        ride.setDepartureDatetime(Instant.now().plusSeconds(3600));
        ride.setRideStatus(available);
        ride.setRideDescription("This is a test description");
        ride.setCostPerSeat(10f);
        rideRepository.save(ride);

        token = jwtService.generateToken(driver.getId(), Set.of(driver.getRole().getName()));
    }

    @Test
    public void testGetRides() {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + "/ride")
                .queryParam("userLat", 48.7758)
                .queryParam("userLng", 9.1829)
                .queryParam("destLat", 48.7800)
                .queryParam("destLng", 9.1900)
                .queryParam("radius", 10)
                .queryParam("requiredSeats", 2)
                .queryParam("departureDateTime", Instant.now())
                .queryParam("seats", 2)
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Add JWT Token in Authorization header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<RideResponse[]> response = testRestTemplate.exchange(url, HttpMethod.GET, request, RideResponse[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(Objects.requireNonNull(response.getBody()).length > 0);
    }

}
