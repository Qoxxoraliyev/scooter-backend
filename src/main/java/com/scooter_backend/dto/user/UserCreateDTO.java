package com.scooter_backend.dto.user;
import com.scooter_backend.enums.Role;
import jakarta.validation.constraints.*;

public record UserCreateDTO(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 100)
        String fullName,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[+]?[0-9]{9,15}$")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100)
        String password,

        @NotNull(message = "Role is required")
        Role role
) {}
