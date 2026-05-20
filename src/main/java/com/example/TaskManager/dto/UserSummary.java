package com.example.TaskManager.dto;

import com.example.TaskManager.model.Role;

public record UserSummary(
        Long id,
        String fullName,
        String email,
        Role role
) {
}
