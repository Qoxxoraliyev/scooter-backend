package com.scooter_backend.service;

import com.scooter_backend.dto.ride.*;

import java.math.BigDecimal;
import java.util.List;

public interface RideService {

    RideResponseDTO startRide(Long userId, RideStartDTO dto);

    RideResponseDTO finishRide(RideFinishDTO dto);

    RideResponseDTO getById(Long rideId);

    BigDecimal calculateCost(Long scooterId, Double distance);

    List<RideResponseDTO> getAllRides();

    List<RideResponseDTO> getUserRides(Long userId);

    List<RideResponseDTO> getDriverRides(Long driverId);

    void cancelRide(Long rideId);

}