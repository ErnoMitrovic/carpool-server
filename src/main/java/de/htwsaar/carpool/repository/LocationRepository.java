package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Location;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    @Query(value = """
            SELECT l
            FROM Location l
            WHERE ST_EQUALS(l.position, :position) = true
            """)
    Optional<Location> findByPosition(@Param("position") Geometry position);
}
