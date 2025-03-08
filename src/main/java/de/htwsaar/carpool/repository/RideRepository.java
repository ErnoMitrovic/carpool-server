package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Ride;
import org.locationtech.jts.geom.Point;
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
     * @param radius            Radius in which the start and end locations should be in km
     * @param departureDatetime Departure datetime
     * @return List of available rides
     */
    @Query(value = """
                            SELECT r FROM Ride r
                            JOIN Location l_start ON r.start = l_start
                            JOIN Location l_end ON r.end = l_end
                            WHERE r.availableSeats >= 1
                            AND DISTANCE(l_end.position, :destination) <= :radius
                            AND DISTANCE(l_start.position, :userLocation) <= :radius
                            AND r.departureDatetime >= :departureDatetime
                            AND r.rideStatus.name = "AVAILABLE"
                            ORDER BY DISTANCE(l_start.position, :userLocation) ASC, r.departureDatetime ASC
            """)
    List<Ride> findAvailableRides(
            @Param("userLocation") Point userLocation,
            @Param("destination") Point destination,
            @Param("radius") double radius,
            @Param("departureDatetime") Instant departureDatetime);

    /**
     * The method finds available rides that match the given criteria.
     *
     * @param rideId   the ride id
     * @param driverId the driver id
     * @return true if the ride exists
     */
    boolean existsByIdAndDriverId(Long rideId, String driverId);
}
