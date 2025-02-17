package de.htwsaar.carpool.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "1", authorities = "USER", password = "raw")
public class BookingControllerTest {
    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Used for JSON serialization

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

    @Test
    void createBooking_ShouldReturn201_WhenValid() throws Exception {
        String url = "/api/" + apiVersion + "/ride/3/booking";

        // Act & Assert: Send POST request to API
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createBooking_ShouldReturn404_WhenRideNotFound() throws Exception {
        String url = "/api/" + apiVersion + "/ride/9999/booking";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Rides not found"));
    }

    @Test
    void createBooking_ShouldReturn400_WhenSeatsUnavailable() throws Exception {
        // Simulating a ride with no available seats
        String url = "/api/" + apiVersion + "/ride/4/booking";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ShouldReturn409_WhenUserAlreadyBooked() throws Exception {
        String url = "/api/" + apiVersion + "/ride/1/booking";
        // Assuming user already booked this ride

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}