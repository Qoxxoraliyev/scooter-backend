package com.scooter_backend.dto.driver;

public record DriverLocationDTO (
        Long driverId,
        Double lat,
        Double lon,
        Long ts
){}
