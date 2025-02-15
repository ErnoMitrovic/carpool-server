package de.htwsaar.carpool.domain.ride;

import de.htwsaar.carpool.domain.location.PointDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.io.Serializable;

import static de.htwsaar.carpool.config.Constants.DATE_TIME_FORMAT;
import static de.htwsaar.carpool.config.Constants.DATE_TIME_REGEX;

@Builder
public record GetRidesRequest(
        @NotNull @Min(0)
        Double radius,
        @NotNull @Pattern(regexp = DATE_TIME_REGEX,
        message = "The pattern must match " + DATE_TIME_FORMAT)
        String departureDateTime,
        @NotNull
        PointDTO startLocation,
        @NotNull
        PointDTO endLocation) implements Serializable {
}
