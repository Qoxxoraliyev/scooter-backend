package com.scooter_backend.dto.auth;

public record LoginRequest(
        String fullName,
        String phone,
        String password
) {}
