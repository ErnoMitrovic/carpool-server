package de.htwsaar.carpool.dto.ride;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideSortDTO implements Serializable {
    // For pagination
    private int page;
    private int size;

    @NotNull
    private LocalTime departureTime;
    @NotNull
    private Point startLocation;
    @NotNull
    private Point endLocation;
    private int seats;
}
