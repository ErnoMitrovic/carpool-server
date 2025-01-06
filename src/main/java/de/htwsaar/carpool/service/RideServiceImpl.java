package de.htwsaar.carpool.service;

import de.htwsaar.carpool.dto.ApiResponseDTO;
import de.htwsaar.carpool.dto.ApiResponseStatus;
import de.htwsaar.carpool.dto.ride.RideDTO;
import de.htwsaar.carpool.dto.ride.RideSortDTO;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.repository.RideRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RideServiceImpl implements RideService {
    private static final double POSITION_END_THRESHOLD = 0.01;
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    public RideServiceImpl(RideRepository rideRepository, ModelMapper modelMapper) {
        this.rideRepository = rideRepository;
        this.modelMapper = modelMapper;
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
        ).stream().map(ride -> modelMapper.map(ride, RideDTO.class)).toList();

        if(rides.isEmpty()) {
            throw new RideNotFoundException("No rides found");
        }

        return ResponseEntity.ok(
                new ApiResponseDTO<>(ApiResponseStatus.SUCCESS, rides));
    }
}
