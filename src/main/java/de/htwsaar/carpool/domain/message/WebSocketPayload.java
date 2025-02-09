package de.htwsaar.carpool.domain.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Schema(description = "WebSocket message payload structure")
@Data
@NoArgsConstructor
public class WebSocketPayload {

    @Schema(description = "Type of the WebSocket message", required = true, example = "MESSAGE")
    @NonNull
    private WebSocketPayloadType type;

    @Schema(description = "Sender's user ID (required for MESSAGE type)", example = "user123")
    private String senderId;
    @Schema(description = "Message content (required for MESSAGE type)", example = "Hello, how are you?")
    private String content;
    @Schema(description = "Message ID (required for STATUS_UPDATE type)", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID messageId;
    @Schema(description = "Message status (required for STATUS_UPDATE type)", example = "READ")
    private MessageStatus status;

    @Builder
    public WebSocketPayload(@NonNull WebSocketPayloadType type, String content, String senderId, UUID messageId, MessageStatus status) {
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
    public static WebSocketPayload forMessage(@NonNull String senderId, @NonNull String content) {
        return WebSocketPayload.builder()
                .type(WebSocketPayloadType.MESSAGE)
                .senderId(senderId)
                .content(content)
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
