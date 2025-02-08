package de.htwsaar.carpool.domain.mesage;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
public class WebSocketPayload {

    @NonNull
    private WebSocketPayloadType type;

    private String senderId;          // For MESSAGE type
    private String content;         // For MESSAGE type
    private UUID messageId;         // For STATUS_UPDATE type
    private MessageStatus status;   // For STATUS_UPDATE type

    public WebSocketPayload(WebSocketPayloadType type, String content, String senderId, UUID messageId, MessageStatus status) {
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.messageId = messageId;
        this.status = status;
        validate(); // Ensure the payload is valid on creation
    }

    public void validate() {
        if (type == WebSocketPayloadType.MESSAGE) {
            if (content == null || content.isEmpty()) {
                throw new IllegalArgumentException("Content must be provided for MESSAGE type.");
            }
            if (senderId == null || senderId.isEmpty()) {
                throw new IllegalArgumentException("SenderId must be provided for MESSAGE type.");
            }
            if (messageId != null || status != null) {
                throw new IllegalArgumentException("MessageId and Status must be null for MESSAGE type.");
            }
        } else if (type == WebSocketPayloadType.STATUS_UPDATE) {
            if (messageId == null) {
                throw new IllegalArgumentException("MessageId must be provided for STATUS_UPDATE type.");
            }
            if (status == null) {
                throw new IllegalArgumentException("Status must be provided for STATUS_UPDATE type.");
            }
            if (content != null || senderId != null) {
                throw new IllegalArgumentException("Content and SenderId must be null for STATUS_UPDATE type.");
            }
        } else {
            throw new IllegalArgumentException("Unknown WebSocketPayloadType.");
        }
    }

    // Factory methods for creating specific types
    public static WebSocketPayload forMessage(@NonNull String content, @NonNull String senderId) {
        return WebSocketPayload.builder()
                .type(WebSocketPayloadType.MESSAGE)
                .content(content)
                .senderId(senderId)
                .build();
    }

    public static WebSocketPayload forStatusUpdate(@NonNull UUID messageId, @NonNull MessageStatus status) {
        return WebSocketPayload.builder()
                .type(WebSocketPayloadType.STATUS_UPDATE)
                .messageId(messageId)
                .status(status)
                .build();
    }

}
