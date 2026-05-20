package com.example.TaskManager.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalProjects,
        long totalTasks,
        long overdueTasks,
        Map<String, Long> taskCountsByStatus,
        List<TaskResponse> upcomingTasks
) {
}
