package com.scooter_backend.mapper;

import com.scooter_backend.dto.user.UserResponseDTO;
import com.scooter_backend.entity.Driver;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {

        Long scooterId = null;
        DriverStatus status = null;

        if (user.getDriver() != null) {
            Driver driver = user.getDriver();

            status = driver.getStatus();

            if (driver.getScooter() != null) {
                scooterId = driver.getScooter().getId();
            }
        }

        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getEnabled(),
                user.getStatus(),
                scooterId,
                user.getCreatedAt()
        );
    }


}