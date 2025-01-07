package de.htwsaar.carpool.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideDTO implements Serializable {
    private Integer id;
    private Instant departureTime;
    private String startLocation;
    private String endLocation;
    private Integer seats;
    private Double price;
}
