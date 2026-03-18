package com.scooter_backend.dto.user;

import jakarta.validation.constraints.*;

public record UserPasswordUpdateDTO(

        @NotBlank(message = "Old password is required")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 100)
        String newPassword
) {}
