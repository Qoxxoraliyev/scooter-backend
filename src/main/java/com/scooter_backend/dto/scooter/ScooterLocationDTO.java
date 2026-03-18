package com.scooter_backend.dto.scooter;

import jakarta.validation.constraints.*;

public record ScooterLocationDTO(

        @NotNull
        Long scooterId,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude

) {}