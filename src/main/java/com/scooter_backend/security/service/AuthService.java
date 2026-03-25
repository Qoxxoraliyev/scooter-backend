package com.scooter_backend.security.service;

import com.scooter_backend.dto.auth.*;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.Role;
import com.scooter_backend.exception.CustomException;
import com.scooter_backend.repository.UserRepository;
import com.scooter_backend.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlacklistService blacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
    }

    // 🔐 REGISTER
    public AuthResponse register(LoginRequest request) {

        if (userRepository.existsByPhone(request.phone())) {
            throw new CustomException("Phone already registered");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        userRepository.save(user);

        String access = jwtService.generateAccessToken(user.getPhone(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getPhone(), user.getRole().name());

        return new AuthResponse(access, refresh);
    }

    //  LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException("Invalid password");
        }

        if (!user.getEnabled()) {
            throw new CustomException("User is disabled");
        }

        String access = jwtService.generateAccessToken(user.getPhone(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getPhone(), user.getRole().name());

        return new AuthResponse(access, refresh);
    }

    //  REFRESH
    public AuthResponse refresh(String refreshToken) {

        String phone = jwtService.extractPhone(refreshToken);

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new CustomException("User not found"));

        if (!jwtService.isValid(refreshToken, new UsersDetails(user))) {
            throw new CustomException("Invalid refresh token");
        }

        String newAccess = jwtService.generateAccessToken(phone,user.getRole().name());

        return new AuthResponse(newAccess, refreshToken);
    }

    // 🚪 LOGOUT
    public void logout(String token) {

        if (token == null || token.isBlank()) {
            throw new CustomException("Token required");
        }

        blacklistService.blacklist(token);
    }


}