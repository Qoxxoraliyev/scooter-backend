package com.scooter_backend.dto.scooter;

import jakarta.validation.constraints.NotNull;

public record AssignScooterDTO(

        @NotNull(message = "Scooter id is required")
        Long scooterId
) {}
