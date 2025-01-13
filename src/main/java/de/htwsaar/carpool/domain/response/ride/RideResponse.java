package de.htwsaar.carpool.domain.response.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse implements Serializable {
    private Long id;
    private Instant departureTime;
    private String startLocation;
    private String endLocation;
    private Integer seats;
    private Float price;
}
