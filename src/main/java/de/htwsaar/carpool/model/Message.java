package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "message", indexes = {
        @Index(name = "message_idx_1", columnList = "timestamp")
})
@SequenceGenerator(
        name = "message_id_seq",
        sequenceName = "message_id_seq",
        allocationSize = 1
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @NotNull
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private CarpoolUser sender;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private CarpoolUser receiver;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_status_id", nullable = false)
    private MessageStatus messageStatus;

    /**
     * Pre-persist validation to ensure sender and receiver are not the same.
     */
    @PrePersist
    private void validateSenderReceiver() {
        if (sender != null && receiver != null && sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same user.");
        }
    }

}
