package com.example.TaskManager.dto;

import com.example.TaskManager.model.Role;

public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}
