package com.scooter_backend.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderCreateDTO(

        @NotBlank(message = "Message is required")
        @Size(max = 500,message = "Message must be at most 500 characters")
        String message
) {}
