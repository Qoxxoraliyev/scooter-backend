package com.scooter_backend.dto.user;

public record AdminDriverLocationResponseDTO(
        Long driverId,
        Double lat,
        Double lon,
        Long ts
){}
