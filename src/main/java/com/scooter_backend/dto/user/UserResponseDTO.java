package com.scooter_backend.dto.user;

import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.enums.Role;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String fullName,
        String phone,
        Role role,
        Boolean enabled,
        DriverStatus status,
        LocalDateTime createdAt
) {}

