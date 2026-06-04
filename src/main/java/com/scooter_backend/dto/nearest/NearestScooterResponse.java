package com.scooter_backend.dto.nearest;

public record NearestScooterResponse(

        Long scooterId,
        String scooterName,
        double latitude,
        double longitude,
        double distanceMeters,
        Integer batteryLevel,
        String status
) {
}
