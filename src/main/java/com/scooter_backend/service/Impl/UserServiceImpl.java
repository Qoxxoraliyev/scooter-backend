package com.scooter_backend.service.Impl;

import com.scooter_backend.dto.user.*;
import com.scooter_backend.entity.Driver;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.enums.Role;
import com.scooter_backend.enums.ScooterStatus;
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

        if (dto.role() != Role.DRIVER && dto.scooterId() != null) {
            throw new RuntimeException("Scooter can only be assigned when role is DRIVER");
        }

        User user = new User();
        user.setFullName(dto.fullName());
        user.setPhone(dto.phone());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setEnabled(dto.enabled() != null ? dto.enabled() : true);

        userRepository.save(user);

        if (dto.role() == Role.DRIVER) {
            Scooter scooter;

            if (dto.scooterId() != null) {
                scooter = scooterRepository.findById(dto.scooterId())
                        .orElseThrow(() -> new RuntimeException("Scooter not found"));

                if (Boolean.TRUE.equals(scooter.getDeleted())) {
                    throw new RuntimeException("Scooter is deleted");
                }

                if (scooter.getStatus() != ScooterStatus.ACTIVE) {
                    throw new RuntimeException("Scooter is not active");
                }

                if (scooter.getDriver() != null) {
                    throw new RuntimeException("Scooter already assigned to another driver");
                }

            } else {
                scooter = scooterRepository
                        .findFirstByDriverIsNullAndStatusAndIsLockedFalseAndDeletedFalse(ScooterStatus.ACTIVE)
                        .orElseThrow(() -> new RuntimeException("Mos bo'sh scooter topilmadi"));
            }

            Driver driver = new Driver();
            driver.setUser(user);
            driver.setScooter(scooter);
            driver.setStatus(dto.status() != null ? dto.status() : DriverStatus.INACTIVE);

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
                user.setDriver(driver);
            }

            if (dto.status() != null) {
                driver.setStatus(dto.status());
            } else if (driver.getStatus() == null) {
                driver.setStatus(DriverStatus.INACTIVE);
            }

            if (driver.getScooter() == null) {
                Scooter scooter = scooterRepository
                        .findFirstByDriverIsNullAndStatusAndIsLockedFalseAndDeletedFalse(ScooterStatus.ACTIVE)
                        .orElseThrow(() -> new RuntimeException("Mos bo'sh scooter topilmadi"));

                driver.setScooter(scooter);
                scooter.setDriver(driver);
            }

            driverRepository.save(driver);

        } else {
            Driver driver = user.getDriver();

            if (driver != null) {
                Scooter scooter = driver.getScooter();

                if (scooter != null) {
                    scooter.setDriver(null);
                    driver.setScooter(null);
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


    @Override
    public void assignScooterToDriver(Long userId, Long scooterId) {
        User user = getUser(userId);

        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        Driver driver = driverRepository.findById(userId).orElse(null);

        if (driver == null) {
            driver = new Driver();
            driver.setUser(user);
            driver.setStatus(DriverStatus.INACTIVE);
            user.setDriver(driver);
        }

        Scooter newScooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        if (Boolean.TRUE.equals(newScooter.getDeleted())) {
            throw new RuntimeException("Scooter is deleted");
        }

        if (newScooter.getStatus() != ScooterStatus.ACTIVE) {
            throw new RuntimeException("Scooter is not active");
        }

        if (newScooter.getDriver() != null &&
                !newScooter.getDriver().getUser().getId().equals(userId)) {
            throw new RuntimeException("Scooter already assigned to another driver");
        }

        Scooter oldScooter = driver.getScooter();

        if (oldScooter != null && !oldScooter.getId().equals(newScooter.getId())) {
            oldScooter.setDriver(null);
            driver.setScooter(null);
        }

        driver.setScooter(newScooter);
        newScooter.setDriver(driver);

        driverRepository.save(driver);
    }



}
