package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Data
@Entity
@Table(name = "location", schema = "carpool", indexes = {
        @Index(name = "location_idx_1", columnList = "position")
})
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    // Use hibernate spatial
    @Column(name = "position", columnDefinition = "POINT SRID 4326")
    private Point position;
}