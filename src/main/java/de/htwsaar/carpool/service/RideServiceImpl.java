package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.ApiResponseDTO;
import de.htwsaar.carpool.domain.ApiResponseStatus;
import de.htwsaar.carpool.domain.request.ride.CreateRideDTO;
import de.htwsaar.carpool.domain.response.ride.RideDTO;
import de.htwsaar.carpool.domain.request.ride.RideSortDTO;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.repository.LocationRepository;
import de.htwsaar.carpool.repository.RideRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RideServiceImpl implements RideService {
    private static final double POSITION_END_THRESHOLD = 0.01;
    private final RideRepository rideRepository;
    private final LocationRepository locationRepository;

    public RideServiceImpl(RideRepository rideRepository, LocationRepository locationRepository) {
        this.rideRepository = rideRepository;
        this.locationRepository = locationRepository;
    }


    @Override
    public ResponseEntity<ApiResponseDTO<?>> getFilteredRides(RideSortDTO rideSortDTO) throws RideNotFoundException {

        List<RideDTO> rides = rideRepository.findAvailableRides(
                rideSortDTO.getStartLocation().getX(),
                rideSortDTO.getStartLocation().getY(),
                rideSortDTO.getEndLocation().getX(),
                rideSortDTO.getEndLocation().getY(),
                rideSortDTO.getRadius(),
                rideSortDTO.getSeats(),
                rideSortDTO.getDepartureTime()
        ).stream().map(ride -> {
            RideDTO rideDTO = new RideDTO();
            rideDTO.setId(ride.getId());
            rideDTO.setDepartureTime(ride.getDepartureDatetime());
            rideDTO.setStartLocation(
                    ride.getStartLocation().getPosition().toText());
            rideDTO.setEndLocation(
                    ride.getEndLocation().getPosition().toText());
            rideDTO.setSeats(ride.getAvailableSeats());
            rideDTO.setPrice(ride.getCostPerSeat());
            return rideDTO;
        }).toList();

        if(rides.isEmpty()) {
            throw new RideNotFoundException("No rides found");
        }

        return ResponseEntity.ok(
                new ApiResponseDTO<>(ApiResponseStatus.SUCCESS, rides));
    }

    /**
     * Create a new ride as follows:
     * 1. If the location is not found, create a new location
     * 2. Create a new ride with the given details
     * 3. Return the ride details
     * @param createRideDTO DTO containing ride details
     * @return ResponseEntity containing ApiResponseDTO
     */
    @Override
    public ResponseEntity<ApiResponseDTO<?>> createRide(CreateRideDTO createRideDTO) {


        return null;
    }
}
