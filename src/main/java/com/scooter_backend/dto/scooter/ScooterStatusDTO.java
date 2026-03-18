package com.scooter_backend.dto.scooter;

import com.scooter_backend.enums.ScooterStatus;
import jakarta.validation.constraints.NotNull;

public record ScooterStatusDTO(

        @NotNull(message = "Status is required")
        ScooterStatus status
) {}
