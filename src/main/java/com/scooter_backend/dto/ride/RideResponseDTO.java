package com.scooter_backend.dto.ride;

import com.scooter_backend.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponseDTO(

        Long id,

        LocalDateTime startTime,
        LocalDateTime endTime,

        Double startLat,
        Double startLon,

        Double endLat,
        Double endLon,

        Double distance,
        BigDecimal cost,

        RideStatus status,
        Boolean paid,

        Long userId,
        Long scooterId,

        LocalDateTime createdAt

) {}