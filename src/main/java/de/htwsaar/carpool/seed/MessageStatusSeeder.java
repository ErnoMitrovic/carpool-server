package de.htwsaar.carpool.seed;

import de.htwsaar.carpool.domain.message.MessageStatus;
import de.htwsaar.carpool.repository.MessageStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageStatusSeeder {

    private final MessageStatusRepository messageStatusRepository;

    /**
     * Seeds the `message_status` table with predefined values in the {@link MessageStatus}` enum.
     */
    @PostConstruct
    public void seedMessageStatuses() {
        for (MessageStatus statusValue : MessageStatus.values()) {
            // Check if the status already exists to avoid duplicates
            if (messageStatusRepository.findByName(statusValue.name()).isEmpty()) {
                de.htwsaar.carpool.model.MessageStatus status = new de.htwsaar.carpool.model.MessageStatus();
                status.setName(statusValue.name());
                messageStatusRepository.save(status);
            }
        }
    }
}

