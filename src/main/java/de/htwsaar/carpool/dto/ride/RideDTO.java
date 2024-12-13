package de.htwsaar.carpool.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDTO implements Serializable {
    private long id;
    private LocalTime departureTime;
    private Point startLocation;
    private Point endLocation;
    private int seats;
    private double price;
}
