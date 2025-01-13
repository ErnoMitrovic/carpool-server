package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ride_status", schema = "carpool", uniqueConstraints = {
        @UniqueConstraint(name = "ride_status_ak_1", columnNames = {"name"})
})
@SequenceGenerator(name = "ride_status_id_seq", sequenceName = "ride_status_id_seq", allocationSize = 1)
public class RideStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

}