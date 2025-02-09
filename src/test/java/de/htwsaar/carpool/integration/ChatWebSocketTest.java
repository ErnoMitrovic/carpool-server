package de.htwsaar.carpool.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwsaar.carpool.domain.message.MessageStatus;
import de.htwsaar.carpool.domain.message.RecordedMessage;
import de.htwsaar.carpool.domain.message.WebSocketPayload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class ChatWebSocketTest {

    private static final String WS_URI = "ws://localhost:%d/chat?senderId=testUser1&receiverId=testUser2";
    private static RedisServer embeddedRedis;
    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();

    @LocalServerPort
    private int port;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;

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

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        // Inject the Redis port dynamically
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> embeddedRedis.ports().get(0));
    }

    @BeforeEach
    void setUp() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void testWebSocketChatWithEmbeddedRedis() throws Exception {
        // Create WebSocket session
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

        // Create test message
        WebSocketPayload testPayload = WebSocketPayload.forMessage("testUser1", "Hello World");
        String testMessage = objectMapper.writeValueAsString(testPayload);

        CountDownLatch messageLatch = new CountDownLatch(2); // Expect 2 messages: SENT and DELIVERED

        // Connect to WebSocket
        WebSocketSession session = client.execute(
                new WebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) {
                        try {
                            log.info("Connection established");
                            session.sendMessage(new TextMessage(testMessage));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                        log.info("Received message: {}", message.getPayload());
                        receivedMessages.add(message.getPayload().toString());
                        messageLatch.countDown();
                    }

                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable exception) {
                        log.error("Transport error", exception);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                        log.info("Connection closed: {}", closeStatus);
                    }

                    @Override
                    public boolean supportsPartialMessages() {
                        return false;
                    }
                },
                headers,
                URI.create(String.format(WS_URI, port))
        ).get(5, TimeUnit.SECONDS);

        // Wait for message processing
        assertTrue(messageLatch.await(5, TimeUnit.SECONDS));

        List<RecordedMessage> chatHistory = objectMapper.readValue(
                receivedMessages.get(receivedMessages.size() - 1), // Take the last message
                new TypeReference<List<RecordedMessage>>() {}
        );

        assertFalse(chatHistory.isEmpty());
        RecordedMessage lastMessage = chatHistory.get(chatHistory.size() - 1);
        assertEquals("testUser1", lastMessage.getSenderId());
        assertEquals("Hello World", lastMessage.getContent());
        assertEquals(MessageStatus.DELIVERED, lastMessage.getStatus());

        // *** Redis Chat History Check ***
        RList<RecordedMessage> redisChatHistory = redissonClient.getList("chat:history:individual:testUser1:testUser2");
        assertFalse(redisChatHistory.isEmpty());
        assertEquals(1, redisChatHistory.size());

        for (RecordedMessage message : redisChatHistory) {
            assertEquals("testUser1", message.getSenderId());
            assertEquals("Hello World", message.getContent());
            assertEquals(MessageStatus.DELIVERED, message.getStatus());
        }

        log.info("Redis chat history verified successfully.");

        // Cleanup
        session.close();
    }

}
