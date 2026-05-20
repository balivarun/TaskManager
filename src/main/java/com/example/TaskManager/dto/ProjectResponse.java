package com.example.TaskManager.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDate dueDate,
        LocalDateTime createdAt,
        UserSummary owner,
        List<UserSummary> members,
        long totalTasks,
        long completedTasks
) {
}
