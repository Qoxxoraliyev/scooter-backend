package com.scooter_backend.dto.order;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record OrderCreateDTO(

        @NotBlank
        @Column(nullable = false,name = "from_location")
        String fromLocation,

        @NotBlank
        @Column(nullable = false,name = "to_location")
        String toLocation,

        @NotBlank
        @Column(nullable = false,name = "client_phone")
        String clientPhone
) {}
