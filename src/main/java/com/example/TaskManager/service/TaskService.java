package com.example.TaskManager.service;

import com.example.TaskManager.dto.TaskRequest;
import com.example.TaskManager.dto.TaskResponse;
import com.example.TaskManager.exception.ForbiddenException;
import com.example.TaskManager.exception.NotFoundException;
import com.example.TaskManager.model.Project;
import com.example.TaskManager.model.Role;
import com.example.TaskManager.model.Task;
import com.example.TaskManager.model.TaskStatus;
import com.example.TaskManager.model.User;
import com.example.TaskManager.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, ProjectService projectService, UserService userService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findTasks(Long projectId, User currentUser) {
        if (projectId != null) {
            Project project = projectService.getAccessibleProject(projectId, currentUser);
            return taskRepository.findByProjectOrderByCreatedAtDesc(project).stream().map(this::toResponse).toList();
        }

        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findAll().stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .map(this::toResponse)
                    .toList();
        }

        List<Project> projects = projectService.findAccessibleProjects(currentUser).stream()
                .map(project -> projectService.getProject(project.id()))
                .toList();
        if (projects.isEmpty()) {
            return List.of();
        }
        return taskRepository.findByProjectInOrderByCreatedAtDesc(projects).stream().map(this::toResponse).toList();
    }

    @Transactional
    public TaskResponse create(TaskRequest request, User currentUser) {
        requireAdmin(currentUser);
        Project project = projectService.getProject(request.projectId());
        Task task = new Task();
        updateTaskFields(task, request, project);
        task.setCreatedBy(currentUser);
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long taskId, TaskRequest request, User currentUser) {
        requireAdmin(currentUser);
        Task task = getTask(taskId);
        Project project = projectService.getProject(request.projectId());
        updateTaskFields(task, request, project);
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, TaskStatus status, User currentUser) {
        Task task = getTask(taskId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());
        if (!isAdmin && !isAssignee) {
            throw new ForbiddenException("Only admins or the assigned member can update task status");
        }
        task.setStatus(status);
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignee() == null ? null : userService.toSummary(task.getAssignee()),
                userService.toSummary(task.getCreatedBy())
        );
    }

    private void updateTaskFields(Task task, TaskRequest request, Project project) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setProject(project);
        if (request.assigneeId() != null) {
            User assignee = userService.findById(request.assigneeId());
            boolean isMember = project.getMembers().stream().anyMatch(member -> member.getId().equals(assignee.getId()));
            if (!isMember) {
                throw new ForbiddenException("Assignee must be part of the project team");
            }
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }
    }

    private void requireAdmin(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Admin access required");
        }
    }
}
