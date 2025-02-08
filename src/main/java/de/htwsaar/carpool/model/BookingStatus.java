package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "booking_status", uniqueConstraints = {
        @UniqueConstraint(name = "booking_status_ak_1", columnNames = {"name"})
})
@SequenceGenerator(name = "booking_status_id_seq", sequenceName = "booking_status_id_seq", allocationSize = 1)
public class BookingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_status_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;
}