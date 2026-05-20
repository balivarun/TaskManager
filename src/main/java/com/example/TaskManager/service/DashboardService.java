package com.example.TaskManager.service;

import com.example.TaskManager.dto.DashboardResponse;
import com.example.TaskManager.dto.TaskResponse;
import com.example.TaskManager.model.Project;
import com.example.TaskManager.model.Role;
import com.example.TaskManager.model.TaskStatus;
import com.example.TaskManager.model.User;
import com.example.TaskManager.repository.TaskRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProjectService projectService;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    public DashboardService(ProjectService projectService, TaskRepository taskRepository, TaskService taskService) {
        this.projectService = projectService;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    public DashboardResponse getDashboard(User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Map<String, Long> counts = new HashMap<>();
            for (TaskStatus status : TaskStatus.values()) {
                counts.put(status.name(), taskRepository.countByStatus(status));
            }
            List<TaskResponse> upcoming = taskRepository.findAll().stream()
                    .filter(task -> task.getDueDate() != null)
                    .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
                    .limit(6)
                    .map(taskService::toResponse)
                    .toList();
            return new DashboardResponse(
                    projectService.findAccessibleProjects(currentUser).size(),
                    taskRepository.count(),
                    taskRepository.countByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE),
                    counts,
                    upcoming
            );
        }

        List<Project> projects = projectService.findAccessibleProjects(currentUser).stream()
                .map(project -> projectService.getProject(project.id()))
                .toList();
        Map<String, Long> counts = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            counts.put(status.name(), taskRepository.countByAssigneeAndStatus(currentUser, status));
        }
        List<TaskResponse> upcoming = taskRepository.findByAssigneeOrderByDueDateAsc(currentUser).stream()
                .limit(6)
                .map(taskService::toResponse)
                .toList();
        return new DashboardResponse(
                projects.size(),
                taskRepository.countByAssignee(currentUser),
                taskRepository.countByAssigneeAndDueDateBeforeAndStatusNot(currentUser, LocalDate.now(), TaskStatus.DONE),
                counts,
                upcoming
        );
    }
}
