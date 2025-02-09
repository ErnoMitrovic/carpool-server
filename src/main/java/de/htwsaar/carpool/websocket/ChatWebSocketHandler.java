package de.htwsaar.carpool.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwsaar.carpool.domain.message.MessageStatus;
import de.htwsaar.carpool.domain.message.RecordedMessage;
import de.htwsaar.carpool.domain.message.WebSocketPayload;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final Map<String, RTopic> topics = new ConcurrentHashMap<>();
    private final Map<String, RList<RecordedMessage>> chatHistories = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(RedissonClient redissonClient, ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // Identify the chat based on the WebSocket session (group or private chat)
            String chatId = getChatId(session);
            log.info("New connection established for chatId: " + chatId);

            // Store the WebSocket session in the `activeSessions` map for later use
            activeSessions.put(chatId, session);

            // Publish the entire chat history to the topic
            publishEntireChatHistory(chatId);

            // Get the Redis topic for this chat
            // If the topic doesn't already exist, create a new one
            RTopic topic = topics.computeIfAbsent(chatId, id ->
                    redissonClient.getTopic("chat:" + chatId)
            );

            // Subscribe to the Redis topic and define a listener to handle incoming messages
            int listenerId = topic.addListener(String.class, (channel, msg) -> {
                try {
                    if (session.isOpen()) session.sendMessage(new TextMessage(msg));
                } catch (IOException e) {
                    System.err.println("Failed to send message to WebSocket session: " + e.getMessage());
                }
            });

            // Store the listener ID in the session attributes for later cleanup
            session.getAttributes().put("listenerId", listenerId);
            session.getAttributes().put("chatId", chatId);
        } catch (Exception e) {
            log.error("Error during WebSocket connection establishment. Session ID: {} | Error: {}", session.getId(), e.getMessage(), e);
            if (session.isOpen()) session.close();
        }
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // Parse the incoming message payload
            String payload = message.getPayload();
            WebSocketPayload wsPayload = objectMapper.readValue(payload, WebSocketPayload.class);
            wsPayload.validate();

            switch (wsPayload.getType()) {
                case MESSAGE:
                    handleChatMessage(session, wsPayload);
                    break;
                case STATUS_UPDATE:
                    handleStatusUpdate(session, wsPayload);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported WebSocketPayloadType: " + wsPayload.getType());
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket message. Session ID: {} | Message: {} | Error: {}",
                    session.getId(), message.getPayload(), e.getMessage(), e);
            throw e; // Re-throw for higher-level handlers
        }
    }


    private void handleChatMessage(WebSocketSession session, WebSocketPayload payload) throws Exception {
        // Retrieve the chat ID from the session attributes
        String chatId = (String) session.getAttributes().get("chatId");

        // Generate metadata for the message
        UUID messageId = UUID.randomUUID();

        // Construct the message with metadata
        RecordedMessage recordedMessage = new RecordedMessage(
                messageId,
                payload.getSenderId(),
                chatId,
                payload.getContent(),
                MessageStatus.SENT,
                Instant.now()
        );

        // Save the message to chat history
        RList<RecordedMessage> chatHistory = chatHistories.computeIfAbsent(chatId, id ->
                redissonClient.getList("chat:history:" + id)
        );

        chatHistory.add(recordedMessage);

        // Publish the entire chat history to the topic
        publishEntireChatHistory(chatId);

        // Update the message status to "delivered"
        handleStatusUpdate(session, WebSocketPayload.forStatusUpdate(messageId, MessageStatus.DELIVERED));
        log.info("Message sent and delivered in chat {}: {}", chatId, recordedMessage);
    }

    private void handleStatusUpdate(WebSocketSession session, WebSocketPayload payload) throws Exception {
        String chatId = (String) session.getAttributes().get("chatId");

        // Update the message status in the chat history
        RList<RecordedMessage> chatHistory = chatHistories.get(chatId);
        if (chatHistory == null) {
            log.warn("No chat history found for chatId: {}", chatId);
            return;
        }

        for (int i = chatHistory.size() - 1; i >= 0; i--) {
            RecordedMessage message = chatHistory.get(i);

            if (message.getId().equals(payload.getMessageId())) {
                // Update the message status
                message.setStatus(payload.getStatus());

                // Replace the updated message in the chat history
                chatHistory.set(i, message);

                // Publish the entire chat history to the topic
                publishEntireChatHistory(chatId);

                log.info("Message ID {} status updated to '{}' in chat {}", payload.getMessageId(), payload.getStatus(), chatId);
                return;
            }
        }
        log.warn("Message ID {} not found in chat history for chatId: {}", payload.getMessageId(), chatId);
    }

    private void publishEntireChatHistory(String chatId) {
        try {
            // Retrieve the chat history
            RList<RecordedMessage> chatHistory = chatHistories.computeIfAbsent(chatId, id ->
                    redissonClient.getList("chat:history:" + id)
            );

            if (chatHistory.isEmpty()) {
                log.info("No chat history to publish for chatId: {}", chatId);
                return;
            }

            // Serialize the entire chat history to JSON
            String serializedHistory = objectMapper.writeValueAsString(chatHistory);

            // Publish the entire history to the Redis topic
            RTopic topic = topics.computeIfAbsent(chatId, id ->
                    redissonClient.getTopic("chat:" + id)
            );
            topic.publish(serializedHistory);

            log.info("Published entire chat history for chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Failed to publish chat history for chatId: {}. Error: {}", chatId, e.getMessage(), e);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        try {
            // Retrieve the listener ID and chat ID from session attributes
            Integer listenerId = (Integer) session.getAttributes().get("listenerId");
            String chatId = (String) session.getAttributes().get("chatId");

            if (chatId == null || listenerId == null) {
                log.warn("Missing chatId or listenerId during WebSocket connection closure. Session ID: {}", session.getId());
            }

            // Remove the listener from the Redis topic
            RTopic topic = topics.get(chatId);
            if (topic != null) {
                topic.removeListener(listenerId);
                log.info("Removed Redis listener (ID: {}) for chatId: {}", listenerId, chatId);
            } else {
                log.warn("No Redis topic found for chatId: {}", chatId);
            }

            // Optionally remove the session from active sessions (if stored)
            activeSessions.remove(chatId);
            log.info("WebSocket session removed for chatId: {}", chatId);
        } catch (Exception e) {
            log.error("Error during WebSocket connection closure. Session ID: {} | Error: {}", session.getId(), e.getMessage(), e);
        } finally {
            if (session.isOpen()) {
                session.close(closeStatus);
                log.info("WebSocket session explicitly closed. Session ID: {}", session.getId());
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * Generates a chat ID based on the WebSocket session.
     * - For group chats: "group:{rideId}"
     * - For individual chats: "individual:{senderId}:{receiverId}"
     *
     * @param session The WebSocket session.
     * @return The chat ID.
     * @throws IllegalArgumentException If the required parameters are missing or invalid.
     */
    private String getChatId(WebSocketSession session) throws IllegalArgumentException {
        String rideId = extractQueryParam(session, "rideId");
        String senderId = extractQueryParam(session, "senderId");
        String receiverId = extractQueryParam(session, "receiverId");

        if (rideId != null) {

            // Group chat
            String chatId = "group:" + rideId;
            log.info("Generated group chat ID: {} for rideId: {}", chatId, rideId);
            return chatId;

        } else if (senderId != null && receiverId != null) {

            // Private chat - sort user IDs lexicographically
            String[] sortedIds = new String[]{senderId, receiverId};
            Arrays.sort(sortedIds);
            String chatId = "individual:" + sortedIds[0] + ":" + sortedIds[1];
            log.info("Generated private chat ID: {} for senderId: {} and receiverId: {}", chatId, senderId, receiverId);
            return chatId;

        } else {

            throw new IllegalArgumentException(String.format(
                    "Invalid WebSocket URL. Must include either 'rideId' for group chats or 'senderId' and 'receiverId' for private chats. URI: %s",
                    session.getUri()
            ));

        }
    }

    /**
     * Extracts a query parameter from the WebSocket session URI.
     *
     * @param session The WebSocket session.
     * @param paramName The name of the parameter to extract.
     * @return The parameter value, or null if not found.
     * @throws IllegalArgumentException If the WebSocket URI is invalid.
     */
    private String extractQueryParam(WebSocketSession session, String paramName) {
        // Get the URI from the WebSocket session
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("WebSocket URI is missing.");
        }

        // Extract the query parameter from the URI
        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        String value = uriComponents.getQueryParams().getFirst(paramName);
        if (value == null) {
            log.warn("Query parameter '{}' not found in URI: {}", paramName, uri);
        }
        return value;
    }

}
