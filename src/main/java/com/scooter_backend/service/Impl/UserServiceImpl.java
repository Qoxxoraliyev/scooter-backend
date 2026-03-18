package com.scooter_backend.service.Impl;

import com.scooter_backend.dto.user.*;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.Role;
import com.scooter_backend.mapper.UserMapper;
import com.scooter_backend.repository.UserRepository;
import com.scooter_backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO create(UserCreateDTO dto) {

        if (userRepository.existsByPhone(dto.phone())) {
            throw new RuntimeException("Phone already exists");
        }

        User user = new User();
        user.setFullName(dto.fullName());
        user.setPhone(dto.phone());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());


        if (dto.role() == Role.DRIVER) {
            user.setStatus(com.scooter_backend.enums.DriverStatus.INACTIVE);
        }

        userRepository.save(user);

        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll(String role) {

        List<User> users;

        if (role == null) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findAll()
                    .stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(role))
                    .toList();
        }

        return users.stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {

        User user = getUser(id);

        if (!user.getPhone().equals(dto.phone()) &&
                userRepository.existsByPhone(dto.phone())) {
            throw new RuntimeException("Phone already exists");
        }

        user.setFullName(dto.fullName());
        user.setPhone(dto.phone());

        return UserMapper.toDTO(user);
    }

    @Override
    public void updatePassword(Long id, UserPasswordUpdateDTO dto) {

        User user = getUser(id);

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
    }

    @Override
    public void delete(Long id) {

        User user = getUser(id);
        userRepository.delete(user);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }




}
