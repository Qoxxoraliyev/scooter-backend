package com.scooter_backend.mapper;

import com.scooter_backend.dto.user.UserResponseDTO;
import com.scooter_backend.entity.User;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getEnabled(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}