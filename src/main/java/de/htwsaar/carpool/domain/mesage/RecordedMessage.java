package de.htwsaar.carpool.domain.mesage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordedMessage {
    private UUID id;
    private String senderId;
    private String receiverChatId;
    private String content;
    private MessageStatus status;
    private Instant timestamp;
}
