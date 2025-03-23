package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Ride;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    /**
     * The method finds available rides that match the given criteria.
     *
     * @param userLocation      the current user location
     * @param destination       the destination point
     * @param departureDatetime Departure datetime
     * @return List of available rides
     */
    @Query(value = """
                            SELECT r FROM Ride r
                            JOIN Location l_start ON r.start = l_start
                            JOIN Location l_end ON r.end = l_end
                            WHERE r.availableSeats >= 1
                            AND DISTANCE(GEOGRAPHY(l_start.position), GEOGRAPHY(:userLocation)) <= :radius
                            AND DISTANCE(GEOGRAPHY(l_end.position), GEOGRAPHY(:destination)) <= :radius
                            AND r.departureDatetime >= :departureDatetime
                            AND r.rideStatus.name = "AVAILABLE"
                            ORDER BY DISTANCE(l_start.position, :userLocation) ASC,
                            DISTANCE(l_end.position, :destination) ASC, r.departureDatetime ASC
            """, countQuery = """
            SELECT COUNT(r) FROM Ride r
            WHERE r.availableSeats >= 1
            AND distance(r.start.position, :userLocation) <= :radius
            AND distance(r.end.position, :destination) <= :radius
            AND r.departureDatetime >= :departureDatetime
            AND r.rideStatus.name = 'AVAILABLE'
            """)
    Page<Ride> findAvailableRides(
            @Param("radius") Double radius,
            @Param("userLocation") Point userLocation,
            @Param("destination") Point destination,
            @Param("departureDatetime") Instant departureDatetime,
            Pageable pageable);

    /**
     * The method finds available rides that match the given criteria.
     *
     * @param rideId   the ride id
     * @param driverId the driver id
     * @return true if the ride exists
     */
    boolean existsByIdAndDriverId(Long rideId, String driverId);

    /**
     * The method finds all rides created by the driver.
     *
     * @param driverId the driver id
     * @return List of rides
     */
    List<Ride> findAllByDriverId(String driverId, Sort sort);
}
