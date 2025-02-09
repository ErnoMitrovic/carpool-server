package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.message.WebSocketPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "WebSocket", description = "WebSocket API Documentation")
public class WebSocketSwaggerController {

    @Schema(name = "WebSocketPayload", description = "Payload structure for WebSocket messages")
    @Operation(
            summary = "WebSocket Message Types and Formats",
            description = """
                Supported WebSocket message types and their required fields:
                
                1. Message Type:
                ```json
                {
                    "type": "MESSAGE",
                    "content": "Hello!",
                    "senderId": "user123"
                }
                ```
                
                2. Status Update Type:
                ```json
                {
                    "type": "STATUS_UPDATE",
                    "messageId": "123e4567-e89b-12d3-a456-426614174000",
                    "status": "DELIVERED"
                }
                ```
                
                Connection URLs:
                - Group Chat: `ws://localhost:8080/chat?rideId={rideId}`
                - Private Chat: `ws://localhost:8080/chat?senderId={senderId}&receiverId={receiverId}`
                """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "WebSocket Connection Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketPayload.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Message Payload",
                                                    description = "Example of a chat message",
                                                    value = """
                                                            {
                                                                "type": "MESSAGE",
                                                                "content": "Hello, how are you?",
                                                                "senderId": "user123"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Status Update Payload",
                                                    description = "Example of a message status update",
                                                    value = """
                                                            {
                                                                "type": "STATUS_UPDATE",
                                                                "messageId": "123e4567-e89b-12d3-a456-426614174000",
                                                                "status": "READ"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid Message Format")
            }
    )
    @GetMapping("/api/websocket-info")
    public String websocketInfo(
            @Parameter(description = "Group chat ID", example = "ride123")
            @RequestParam(required = false) String rideId,

            @Parameter(description = "Sender's user ID", example = "user123")
            @RequestParam(required = false) String senderId,

            @Parameter(description = "Receiver's user ID", example = "user456")
            @RequestParam(required = false) String receiverId)
    {
        if (rideId != null) {
            return "WebSocket URL: ws://localhost:8080/chat?rideId=" + rideId;
        } else if (senderId != null && receiverId != null) {
            return "WebSocket URL: ws://localhost:8080/chat?senderId=" + senderId + "&receiverId=" + receiverId;
        } else {
            return "Invalid parameters. Provide either rideId or senderId & receiverId.";
        }
    }
}
