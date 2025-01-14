package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Size(max = 255)
    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @NotNull
    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_status_id", nullable = false)
    private MessageStatus messageStatus;

}