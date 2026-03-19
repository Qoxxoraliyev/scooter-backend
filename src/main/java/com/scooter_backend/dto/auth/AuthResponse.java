package com.scooter_backend.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
