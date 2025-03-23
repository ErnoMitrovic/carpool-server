package de.htwsaar.carpool.integration;

import com.redis.testcontainers.RedisContainer;
import de.htwsaar.carpool.TestSecurityConfig;
import de.htwsaar.carpool.domain.booking.BookingStatusValue;
import de.htwsaar.carpool.repository.RideRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "U1", authorities = "USER")
@Import(TestSecurityConfig.class)
public class BookingControllerTest {
    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RideRepository rideRepository;

    private final Long RIDE_ID = 1L;
    private final Long BOOKING_ID = 1L;

    static final GenericContainer<?> redis = new RedisContainer("redis:6.2.6")
            .withExposedPorts(6379)
            .withReuse(true);

    @BeforeAll
    static void startRedis() {
        redis.start();
    }

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () ->
                redis.getHost().startsWith("tcp://") ? "host.docker.internal" : redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    private String getBaseUrl(long rideId) {
        return String.format("/api/%s/ride/%d/booking", apiVersion, rideId);
    }

    @Test
    void createBooking_ShouldReturn201_WhenValid() throws Exception {
        String url = getBaseUrl(3);

        // Act & Assert: Send POST request to API
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createBooking_ShouldReturn404_WhenRideNotFound() throws Exception {
        String url = getBaseUrl(9999);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Rides not found"));
    }

    @Test
    void createBooking_ShouldReturn400_WhenSeatsUnavailable() throws Exception {
        // Simulating a ride with no available seats
        String url = getBaseUrl(4);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ShouldReturn409_WhenUserAlreadyBooked() throws Exception {
        String url = getBaseUrl(1);
        // Assuming user already booked this ride

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "U2", authorities = "DRIVER")
    void getBookings_ShouldReturn200_WhenValid() throws Exception {
        String url = UriComponentsBuilder.fromUriString(getBaseUrl(RIDE_ID))
                .queryParam("statusValue", BookingStatusValue.PENDING)
                .encode().toUriString();

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].bookingId").value(1));
    }


    @Test
    @WithMockUser(username = "U2", authorities = "DRIVER")
    void updateBookingStatus_AcceptBooking_Returns200() throws Exception {
        mockMvc.perform(patch(getBaseUrl(RIDE_ID) + "/{bookingId}", BOOKING_ID)
                        .param("status", BookingStatusValue.ACCEPTED.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.rideId").value(RIDE_ID))
                .andExpect(jsonPath("$.bookingId").value(BOOKING_ID));
    }

    @Test
    @WithMockUser(username = "2", authorities = "DRIVER")
    void updateBookingStatus_RejectBooking_Returns200() throws Exception {
        mockMvc.perform(patch(getBaseUrl(RIDE_ID) + "/{bookingId}", BOOKING_ID)
                        .param("status", BookingStatusValue.REJECTED.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("REJECTED"))
                .andExpect(jsonPath("$.rideId").value(RIDE_ID))
                .andExpect(jsonPath("$.bookingId").value(BOOKING_ID));
    }

    @Test
    @WithMockUser(username = "U2", authorities = "DRIVER")
    void updateBookingStatus_FailWhenRideIsFull_Returns400() throws Exception {
        // Ride with 0 seats available (London Trip)
        long FULL_RIDE_ID = 4L;
        mockMvc.perform(patch(getBaseUrl(FULL_RIDE_ID) + "/{bookingId}", BOOKING_ID)
                        .param("status", BookingStatusValue.ACCEPTED.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "U2", authorities = "DRIVER")
    void updateBookingStatus_FailWhenBookingNotFound_Returns404() throws Exception {
        Long nonExistentBookingId = 999L;

        mockMvc.perform(patch(getBaseUrl(RIDE_ID) + "/{bookingId}", nonExistentBookingId)
                        .param("status", BookingStatusValue.ACCEPTED.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}