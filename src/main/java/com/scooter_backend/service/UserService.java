package com.scooter_backend.service;

import com.scooter_backend.dto.user.UserCreateDTO;
import com.scooter_backend.dto.user.UserPasswordUpdateDTO;
import com.scooter_backend.dto.user.UserResponseDTO;
import com.scooter_backend.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO create(UserCreateDTO dto);

    List<UserResponseDTO> getAll(String role);

    UserResponseDTO update(Long id, UserUpdateDTO dto);

    void updatePassword(Long id, UserPasswordUpdateDTO dto);

    void delete(Long id);

}
