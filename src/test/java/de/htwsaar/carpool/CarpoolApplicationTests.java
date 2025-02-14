package de.htwsaar.carpool;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("test")
class CarpoolApplicationTests {

	private static RedisServer embeddedRedis;

	@BeforeAll
	static void startEmbeddedRedis() throws IOException {
		embeddedRedis = RedisServer.newRedisServer().build();
		embeddedRedis.start();
	}

	@AfterAll
	static void stopEmbeddedRedis() throws IOException {
		if (embeddedRedis != null) {
			embeddedRedis.stop();
		}
	}

	@Test
	void contextLoads() {
	}

}
