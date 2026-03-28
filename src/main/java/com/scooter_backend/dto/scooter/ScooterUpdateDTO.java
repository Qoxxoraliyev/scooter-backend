package com.scooter_backend.dto.scooter;

import com.scooter_backend.enums.ScooterStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ScooterUpdateDTO(

        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotNull(message = "Status is required")
        ScooterStatus status,

        @NotNull(message = "Battery level is required")
        @Min(value = 0, message = "Battery cannot be less than 0")
        @Max(value = 100, message = "Battery cannot be more than 100")
        Integer batteryLevel,

        @NotNull(message = "Lock status is required")
        Boolean locked,

        @NotNull(message = "Price per km is required")
        @Positive(message = "Price must be positive")
        BigDecimal pricePerKm
) {}
