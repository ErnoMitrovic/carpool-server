package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Location;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Find a location by its position within a radius of 10 meters.
     * @param position The position to search for.
     * @return The location if found.
     */
    @Query(value = """
            SELECT l
            FROM Location l
            WHERE distance(geography(l.position), geography(:position)) < 10
            """)
    Optional<Location> findByPosition(@Param("position") Point position);
}
