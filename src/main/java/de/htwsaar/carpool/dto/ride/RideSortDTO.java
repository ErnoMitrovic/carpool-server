package de.htwsaar.carpool.dto.ride;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
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
    // The radius cannot be negative
    @NotNull
    @Min(0)
    private Double radius;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private String departureTime;
    @NotNull
    private Point startLocation;
    @NotNull
    private Point endLocation;
    private int seats;
}
