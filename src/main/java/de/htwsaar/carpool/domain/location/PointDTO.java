package de.htwsaar.carpool.domain.location;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

/**
 * PointDTO is a data class that represents a point in a map.
 * @param x The x-coordinate of the point
 * @param y The y-coordinate of the point
 */
@Builder
public record PointDTO(@NotNull Double x, @NotNull Double y) implements Serializable {
}
