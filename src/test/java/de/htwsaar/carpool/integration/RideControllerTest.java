package de.htwsaar.carpool.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwsaar.carpool.controller.RideController;
import de.htwsaar.carpool.domain.ride.RideResponse;
import de.htwsaar.carpool.domain.ride.UpdateRideRequest;
import de.htwsaar.carpool.exceptions.RideNotFoundException;
import de.htwsaar.carpool.exceptions.UnauthorizedDriverException;
import de.htwsaar.carpool.service.RideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
@AutoConfigureMockMvc
public class RideControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RideService rideService;

    @Test
    public void testUpdateRide_Success() throws Exception {
        UpdateRideRequest request = new UpdateRideRequest(
                "2025-01-15T10:00Z",
                4,
                15.0f,
                "Updated description",
                null,
                null,
                1L);

        RideResponse rideResponse = new RideResponse(
                1L,
                "2025-01-15T10:00Z",
                "POINT(-74.006 40.7128)",
                "POINT(-118.2437 34.0522)",
                4,
                15.0f);

        when(rideService.updateRide(anyLong(), any(UpdateRideRequest.class)))
                .thenReturn(ResponseEntity.ok(rideResponse));

        mockMvc.perform(put("/ride/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.seats").value(4));
    }

    @Test
    public void testUpdateRideStatus_Success() throws Exception {
        when(rideService.cancelRide(1L, 1L))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/ride/1/?driverId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateRideStatus_RideNotFound() throws Exception {
        when(rideService.cancelRide(999L, 1L))
                .thenThrow(new RideNotFoundException("Ride not found"));

        mockMvc.perform(delete("/ride/999/?driverId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ride not found"));
    }

    @Test
    public void testUpdateRideStatus_UnauthorizedDriver() throws Exception {

        when(rideService.cancelRide(1L, 2L))
                .thenThrow(new UnauthorizedDriverException("Driver not authorized to update this ride"));

        mockMvc.perform(delete("/ride/1/?driverId=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Driver not authorized to update this ride"));
    }
}
