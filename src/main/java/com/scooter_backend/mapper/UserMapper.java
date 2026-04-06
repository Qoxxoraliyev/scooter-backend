package com.scooter_backend.mapper;

import com.scooter_backend.dto.user.UserResponseDTO;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {
        Long scooterId = null;
        DriverStatus status = null;

        if (user.getDriver() != null) {
            status = user.getDriver().getStatus();

            if (user.getDriver().getScooter() != null) {
                scooterId = user.getDriver().getScooter().getId();
            }
        }

        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getEnabled(),
                user.getCreatedAt(),
                status,
                scooterId
        );
    }
}