package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "message_status", uniqueConstraints = {
        @UniqueConstraint(name = "message_status_ak_1", columnNames = {"name"})
})
@SequenceGenerator(name = "message_status_id_seq", sequenceName = "message_status_id_seq", allocationSize = 1)
public class MessageStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

}