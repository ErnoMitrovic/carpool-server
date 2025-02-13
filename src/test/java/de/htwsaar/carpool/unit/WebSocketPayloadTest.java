package de.htwsaar.carpool.unit;

import de.htwsaar.carpool.domain.message.MessageStatus;
import de.htwsaar.carpool.domain.message.WebSocketPayload;
import de.htwsaar.carpool.domain.message.WebSocketPayloadType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketPayloadTest {

    @Test
    void testValidMessagePayload() {
        WebSocketPayload payload = WebSocketPayload.forMessage("user123", "Hello, how are you?");
        assertEquals(WebSocketPayloadType.MESSAGE, payload.getType());
        assertEquals("user123", payload.getSenderId());
        assertEquals("Hello, how are you?", payload.getContent());
        assertNull(payload.getMessageId());
        assertNull(payload.getStatus());
    }

    @Test
    void testMessagePayloadMissingContent() {
        assertThrowsNullPointerWithMessage(
                () -> WebSocketPayload.forMessage("user123", null),
                "content is marked non-null but is null"
        );
    }

    @Test
    void testMessagePayloadMissingSenderId() {
        assertThrowsNullPointerWithMessage(
                () -> WebSocketPayload.forMessage(null, "Hello, how are you?"),
                "senderId is marked non-null but is null"
        );
    }

    @Test
    void testMessagePayloadWithMessageIdAndStatus() {
        WebSocketPayload payload = new WebSocketPayload(WebSocketPayloadType.MESSAGE, "Hello", "user123", null, null);
        // Intentionally set messageId and status after creation to simulate an invalid state
        payload.setMessageId(UUID.randomUUID());
        payload.setStatus(MessageStatus.READ);

        assertThrowsWithMessage(
                payload::validate,
                "MessageId and Status must be null for MESSAGE type."
        );
    }

    @Test
    void testValidStatusUpdatePayload() {
        UUID messageId = UUID.randomUUID();
        WebSocketPayload payload = WebSocketPayload.forStatusUpdate(messageId, MessageStatus.READ);
        assertEquals(WebSocketPayloadType.STATUS_UPDATE, payload.getType());
        assertEquals(messageId, payload.getMessageId());
        assertEquals(MessageStatus.READ, payload.getStatus());
        assertNull(payload.getSenderId());
        assertNull(payload.getContent());
    }

    @Test
    void testStatusUpdatePayloadMissingMessageId() {
        assertThrowsNullPointerWithMessage(
                () -> WebSocketPayload.forStatusUpdate(null, MessageStatus.READ),
                "messageId is marked non-null but is null"
        );
    }

    @Test
    void testStatusUpdatePayloadMissingStatus() {
        UUID messageId = UUID.randomUUID();
        assertThrowsNullPointerWithMessage(
                () -> WebSocketPayload.forStatusUpdate(messageId, null),
                "status is marked non-null but is null"
        );
    }

    @Test
    void testStatusUpdatePayloadWithContentAndSenderId() {
        UUID messageId = UUID.randomUUID();
        WebSocketPayload payload = new WebSocketPayload(WebSocketPayloadType.STATUS_UPDATE, null, null, messageId, MessageStatus.READ);

        // Manually set content and senderId after creation to simulate an invalid state
        payload.setContent("content");
        payload.setSenderId("user123");

        assertThrowsWithMessage(
                payload::validate,
                "Content and SenderId must be null for STATUS_UPDATE type."
        );
    }

    // Utility method to assert NullPointerException
    private void assertThrowsNullPointerWithMessage(Executable executable, String expectedMessage) {
        NullPointerException exception = assertThrows(NullPointerException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    // Utility method to assert IllegalArgumentException
    private void assertThrowsWithMessage(Executable executable, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

}
