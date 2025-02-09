package de.htwsaar.carpool.domain.message;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of WebSocket messages")
public enum WebSocketPayloadType {
    @Schema(description = "Regular chat message")
    MESSAGE,
    @Schema(description = "Message status update (delivered, read, etc.)")
    STATUS_UPDATE
}
