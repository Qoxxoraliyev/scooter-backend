package com.scooter_backend.controller;

import com.scooter_backend.dto.auth.AuthResponse;
import com.scooter_backend.dto.auth.LoginRequest;
import com.scooter_backend.security.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody LoginRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }


}
