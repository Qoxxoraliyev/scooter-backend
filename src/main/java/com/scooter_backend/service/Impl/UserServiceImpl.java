package com.scooter_backend.service.Impl;

import com.scooter_backend.dto.user.*;
import com.scooter_backend.entity.Driver;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.enums.Role;
import com.scooter_backend.mapper.UserMapper;
import com.scooter_backend.repository.DriverRepository;
import com.scooter_backend.repository.ScooterRepository;
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
    private final DriverRepository driverRepository;
    private final ScooterRepository scooterRepository;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, DriverRepository driverRepository, ScooterRepository scooterRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.driverRepository = driverRepository;
        this.scooterRepository = scooterRepository;
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

        userRepository.save(user);

        if (dto.role() == Role.DRIVER) {
            if (dto.scooterId() == null) {
                throw new RuntimeException("Driver uchun scooterId majburiy");
            }

            Scooter scooter = scooterRepository.findById(dto.scooterId())
                    .orElseThrow(() -> new RuntimeException("Scooter topilmadi"));

            if (scooter.getDriver() != null) {
                throw new RuntimeException("Bu scooter allaqachon boshqa driverga biriktirilgan");
            }

            Driver driver = new Driver();
            driver.setUser(user);
            driver.setScooter(scooter);
            driver.setStatus(DriverStatus.INACTIVE);

            user.setDriver(driver);
            scooter.setDriver(driver);

            driverRepository.save(driver);
        }

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
        user.setRole(dto.role());
        user.setEnabled(dto.enabled());

        if (dto.role() == Role.DRIVER) {
            Driver driver = user.getDriver();
            if (driver == null) {
                driver = new Driver();
                driver.setUser(user);
                driver.setStatus(dto.status() != null ? dto.status() : DriverStatus.INACTIVE);
                user.setDriver(driver);
            }
            if (dto.scooterId() == null) {
                throw new RuntimeException("Driver uchun scooterId majburiy");
            }
            Scooter scooter = scooterRepository.findById(dto.scooterId())
                    .orElseThrow(() -> new RuntimeException("Scooter topilmadi"));
            if (scooter.getDriver() != null && !scooter.getDriver().getId().equals(user.getId())) {
                throw new RuntimeException("Bu scooter allaqachon boshqa driverga biriktirilgan");
            }
            driver.setScooter(scooter);
            scooter.setDriver(driver);
            if (dto.status() != null) {
                driver.setStatus(dto.status());
            }
            driverRepository.save(driver);
        } else {
            Driver driver = user.getDriver();

            if (driver != null) {
                Scooter scooter = driver.getScooter();
                if (scooter != null) {
                    scooter.setDriver(null);
                }
                user.setDriver(null);
                driverRepository.delete(driver);
            }
        }
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }



    @Override
    public void updatePassword(Long id, UserPasswordUpdateDTO dto) {

        User user = getUser(id);
        System.out.println(dto.oldPassword());
        System.out.println(user.getPassword());

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
