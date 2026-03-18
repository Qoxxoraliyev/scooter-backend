package com.scooter_backend.dto.ride;

import jakarta.validation.constraints.*;

public record RideFinishDTO(

        @NotNull(message = "Ride ID is required")
        Long rideId,

        @NotNull(message = "End latitude is required")
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double endLat,

        @NotNull(message = "End longitude is required")
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double endLon,

        @PositiveOrZero(message = "Distance cannot be negative")
        Double distance

) {}