package de.htwsaar.carpool.domain.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(description = "WebSocket location payload structure")
@Data
@NoArgsConstructor
public class ShareableLocationPayload {

    @Schema(description = "User ID of the sender (can be a driver or rider)", example = "user123", nullable = true)
    private String userId;

    @Schema(description = "Latitude of the driver's current location", example = "52.5200")
    private double latitude;

    @Schema(description = "Longitude of the driver's current location", example = "13.4050")
    private double longitude;

    @Schema(description = "Original timestamp provided by the client", example = "2024-03-22T14:30:00Z", nullable = true)
    private Instant clientTimestamp;

    @Schema(description = "Timestamp recorded by the server when it received the location", example = "2024-03-22T14:30:02Z", nullable = true)
    private Instant serverTimestamp;

}
