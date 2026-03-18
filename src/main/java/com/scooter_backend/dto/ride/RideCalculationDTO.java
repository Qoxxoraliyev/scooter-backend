package com.scooter_backend.dto.ride;

public record RideCalculationDTO(
        double distanceKm,
        double durationMin
) {}