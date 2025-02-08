package de.htwsaar.carpool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private final ObjectMapper objectMapper;

    public RedissonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        JsonJacksonCodec codec = new JsonJacksonCodec(objectMapper);

        config.setCodec(codec); // Set the custom codec
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);

        return Redisson.create(config);
    }
}
