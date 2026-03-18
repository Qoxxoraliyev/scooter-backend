package com.scooter_backend.dto.ride;

public record RideCalculationRequestDTO(
        double startLat,
        double startLon,
        double endLat,
        double endLon
) {}
