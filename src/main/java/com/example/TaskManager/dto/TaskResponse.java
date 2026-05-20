package com.example.TaskManager.dto;

import com.example.TaskManager.model.Priority;
import com.example.TaskManager.model.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Priority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        Long projectId,
        String projectName,
        UserSummary assignee,
        UserSummary createdBy
) {
}
