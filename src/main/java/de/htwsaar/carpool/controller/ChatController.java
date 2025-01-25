package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.mesage.RecordedMessage;
import lombok.AllArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class ChatController {

    private final RedissonClient redissonClient;

    @GetMapping("/chat/history")
    public List<RecordedMessage> getChatHistory(@RequestParam(required = false) String rideId,
                                                @RequestParam(required = false) String senderId,
                                                @RequestParam(required = false) String receiverId) {

        // Validate input parameters
        if ((rideId == null && (senderId == null || receiverId == null)) || (rideId != null && (senderId != null || receiverId != null))) {
            throw new IllegalArgumentException("Provide either 'rideId' for group chat or both 'senderId' and 'receiverId' for individual chat, but not both.");
        }

        // Construct chatId
        String chatId;
        if (rideId != null) {
            chatId = "group:" + rideId; // Group chat
        } else {
            // Lexicographically order senderId and receiverId to eliminate ambiguity
            String[] participants = new String[]{senderId, receiverId};
            Arrays.sort(participants);
            chatId = "individual:" + participants[0] + ":" + participants[1]; // Individual chat
        }

        // Retrieve chat history
        RList<RecordedMessage> chatHistory = redissonClient.getList("chat:history:" + chatId);
        return chatHistory.readAll();
    }

}
