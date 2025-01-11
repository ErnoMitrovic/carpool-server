package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RideRepository extends JpaRepository<Ride, Integer> {

    /**
     * The method finds available rides that match the given criteria.
     * @param startLng Start longitude
     * @param startLat Start latitude
     * @param endLng End longitude
     * @param endLat End latitude
     * @param radius Radius in which the start and end locations should be
     * @param requiredSeats Number of required seats
     * @param departureDatetime Departure datetime
     * @return List of available rides
     */
    @Query(value = """
            SELECT
                r.*
            FROM
                ride r
            JOIN location l_start ON r.start_location = l_start.id
            JOIN location l_end ON r.end_location = l_end.id
            WHERE
                ST_Distance(l_start.position, POINT(:startLng, :startLat)) <= :radius
                AND ST_Distance(l_end.position, POINT(:endLng, :endLat)) <= :radius
                AND r.departure_datetime >= :departureDatetime
                AND r.available_seats >= :requiredSeats
                AND r.ride_status_id = (SELECT id FROM ride_status WHERE name = 'Available')
            """, nativeQuery = true)
    List<Ride> findAvailableRides(
            @Param("startLng") double startLng,
            @Param("startLat") double startLat,
            @Param("endLng") double endLng,
            @Param("endLat") double endLat,
            @Param("radius") double radius,
            @Param("requiredSeats") int requiredSeats,
            @Param("departureDatetime") String departureDatetime
    );
}
