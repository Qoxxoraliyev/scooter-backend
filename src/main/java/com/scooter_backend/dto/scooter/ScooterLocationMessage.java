package com.scooter_backend.dto.scooter;

public record ScooterLocationMessage(
        Long scooterId,
        double lat,
        double lon,
        long timestamp
){}
