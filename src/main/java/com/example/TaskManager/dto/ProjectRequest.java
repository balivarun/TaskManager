package com.example.TaskManager.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank String name,
        String description,
        LocalDate dueDate
) {
}
