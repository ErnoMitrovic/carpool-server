package de.htwsaar.carpool.domain.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse implements Serializable {
    private Long id;
    private String departureTime;
    private String startLocation;
    private String endLocation;
    private Integer seats;
    private Float price;
}
