package com.scooter_backend.dto.scooter;

import com.scooter_backend.enums.ScooterStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ScooterResponseDTO(

        Long id,
        String name,

        ScooterStatus status,
        Integer batteryLevel,
        Boolean isLocked,

        BigDecimal pricePerKm,

        Double latitude,
        Double longitude,

        LocalDateTime lastServiceDate,
        LocalDateTime createdAt

) {}