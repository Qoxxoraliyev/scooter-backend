package com.scooter_backend.dto.ride;

import jakarta.validation.constraints.*;

public record RideStartDTO(

        @NotNull(message = "Scooter ID is required")
        Long scooterId,

        @NotNull(message = "Start latitude is required")
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double startLat,

        @NotNull(message = "Start longitude is required")
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double startLon

) {}