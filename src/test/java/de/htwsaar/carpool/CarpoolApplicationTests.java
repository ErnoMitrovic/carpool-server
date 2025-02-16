package de.htwsaar.carpool;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class CarpoolApplicationTests {

    static final GenericContainer<?> redis = new RedisContainer("redis:6.2.6")
            .withExposedPorts(6379)
            .withReuse(true);

    static {
        redis.start();
    }

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> {
            log.info("Redis host: {}", redis.getHost());
            if (redis.getHost().startsWith("tcp://")) {
                return "host.docker.internal";
            }
            return redis.getHost();
        });
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @AfterAll
    static void stopRedis() {
        redis.stop();
    }

    @Test
    void containerIsRunning() {
        assertThat(redis.isRunning()).isTrue();
    }

}
