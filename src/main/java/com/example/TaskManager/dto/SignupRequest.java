package com.example.TaskManager.dto;

import com.example.TaskManager.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotNull Role role
) {
}
