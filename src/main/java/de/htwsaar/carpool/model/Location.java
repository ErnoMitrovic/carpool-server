package de.htwsaar.carpool.model;

import de.htwsaar.carpool.config.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "location", indexes = {
        @Index(name = "location_idx_1", columnList = "position")
})
@SequenceGenerator(name = "location_id_seq", sequenceName = "location_id_seq", allocationSize = 1)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position", columnDefinition = "geometry(Point, " + Constants.SRID + ")",
            nullable = false)
    private Point position;

    @Size(max = 100)
    @Column(name = "address")
    private String address;
}