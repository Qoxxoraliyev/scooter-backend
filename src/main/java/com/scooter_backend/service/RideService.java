package com.scooter_backend.service;

import com.scooter_backend.dto.ride.*;

import java.math.BigDecimal;

public interface RideService {

    RideResponseDTO startRide(Long userId, RideStartDTO dto);

    RideResponseDTO finishRide(RideFinishDTO dto);

    RideResponseDTO getById(Long rideId);

    BigDecimal calculateCost(Long scooterId, Double distance);

}