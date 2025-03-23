package de.htwsaar.carpool.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwsaar.carpool.domain.location.ShareableLocationPayload;
import de.htwsaar.carpool.service.UserService;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(LocationWebSocketHandler.class);
    private final RedissonClient redissonClient;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Map<String, RTopic> topics = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public LocationWebSocketHandler(RedissonClient redissonClient, UserService userService, ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractQueryParam(session, "userId");
        String rideId = extractQueryParam(session, "rideId");

        if (userId == null || rideId == null) {
            throw new IllegalArgumentException("Missing required query parameters: driverId or rideId");
        }

        String topicName = "location:" + rideId;
        RTopic topic = topics.computeIfAbsent(topicName, redissonClient::getTopic);

        int listenerId = topic.addListener(String.class, (channel, msg) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(msg));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        session.getAttributes().put("listenerId", listenerId);
        session.getAttributes().put("topicName", topicName);
        session.getAttributes().put("userId", userId);
        session.getAttributes().put("rideId", rideId);
        activeSessions.put(topicName, session);

        log.info("User {} connected to ride {} and subscribed to topic: {}", userId, rideId, topicName);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String topicName = (String) session.getAttributes().get("topicName");

        ShareableLocationPayload location = objectMapper.readValue(message.getPayload(), ShareableLocationPayload.class);
        location.setUserId(session.getAttributes().get("userId").toString());
        location.setServerTimestamp(Instant.now());

        String serialized = objectMapper.writeValueAsString(location);
        RTopic topic = redissonClient.getTopic(topicName);
        topic.publish(serialized);

        log.info("Published location update for {}: {}", topicName, serialized);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer listenerId = (Integer) session.getAttributes().get("listenerId");
        String topicName = (String) session.getAttributes().get("topicName");

        if (listenerId != null && topicName != null) {
            RTopic topic = topics.get(topicName);
            if (topic != null) {
                topic.removeListener(listenerId);
                log.info("Removed listener {} from topic {}", listenerId, topicName);
            }
            activeSessions.remove(topicName);
        }

        if (session.isOpen()) session.close();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error: {}", exception.getMessage());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private String extractQueryParam(WebSocketSession session, String paramName) {
        URI uri = session.getUri();
        if (uri == null) throw new IllegalArgumentException("WebSocket URI is missing.");
        return UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst(paramName);
    }
}
