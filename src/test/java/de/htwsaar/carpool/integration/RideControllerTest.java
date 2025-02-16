package de.htwsaar.carpool.integration;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "1", authorities = "USER", password = "raw")
public class RideControllerTest {
    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    static final GenericContainer<?> redis = new RedisContainer("redis:6.2.6")
            .withExposedPorts(6379)
            .withReuse(true);


    @BeforeAll
    static void enableConfig() {
        redis.start();
    }

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () ->
                redis.getHost().startsWith("tcp://") ? "host.docker.internal" : redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    private String getBaseUrl() {
        return String.format("http://localhost:8080/api/%s/ride", apiVersion);
    }

    @Test
    public void testGetRides() throws Exception {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .queryParam("userLat", -73.985428)
                .queryParam("userLng", 40.748817)
                .queryParam("destLat", 2.2943506)
                .queryParam("destLng", 48.8588443)
                .queryParam("radius", 50)
                .queryParam("departureDateTime", "2025-02-20T08:00:00Z")
                .encode().toUriString();

        mockMvc.perform(get(url).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("*").isArray())
                .andExpect(jsonPath("length()").value(2))
                .andExpect(jsonPath("[0].id").value(2));
    }

}
