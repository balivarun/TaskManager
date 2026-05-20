package com.example.TaskManager.dto;

import com.example.TaskManager.model.Priority;
import com.example.TaskManager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank String title,
        String description,
        @NotNull TaskStatus status,
        @NotNull Priority priority,
        LocalDate dueDate,
        @NotNull Long projectId,
        Long assigneeId
) {
}
