package de.htwsaar.carpool.domain.message;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Possible message statuses")
public enum MessageStatus {
    @Schema(description = "Message has been sent to the recipient")
    SENT,
    @Schema(description = "Message has been delivered to the recipient")
    DELIVERED,
    @Schema(description = "Message has been read by the recipient(s)")
    READ
}
