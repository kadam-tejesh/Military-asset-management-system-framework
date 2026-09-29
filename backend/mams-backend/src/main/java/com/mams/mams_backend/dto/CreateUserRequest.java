package com.mams.mams_backend.dto;

import com.mams.mams_backend.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        String email,
        @NotNull RoleName role,
        Long baseId) {}
