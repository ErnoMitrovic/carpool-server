package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.location.CreateLocationRequest;
import de.htwsaar.carpool.domain.location.LocationResponse;
import de.htwsaar.carpool.model.Location;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.service.LocationService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static de.htwsaar.carpool.config.Constants.SRID;

@Component
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private static final GeometryFactory geometryFactory = new GeometryFactory(
            new PrecisionModel(), SRID
    );

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Save location to the database following the business logic
     * 1. Extract latitude and longitude from the point
     * 2. Create a new Location object
     * 3. Build a response entity containing the saved location
     * @param locationRequest DTO containing location details
     * @return ResponseEntity containing LocationResponse DTO
     */
    @Override
    public ResponseEntity<LocationResponse> saveLocation(CreateLocationRequest locationRequest) {
        Point point = geometryFactory.createPoint(
                new Coordinate(
                        locationRequest.longitude(),
                        locationRequest.latitude()
                )
        );

        Location location = new Location();
        location.setPosition(point);

        Location savedLocation = locationRepository.save(location);
        LocationResponse locationResponse = new LocationResponse(
                savedLocation.getId(),
                savedLocation.getPosition().toText()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(locationResponse);
    }
}
