package com.example.TaskManager.service;

import com.example.TaskManager.dto.ProjectRequest;
import com.example.TaskManager.dto.ProjectResponse;
import com.example.TaskManager.exception.ForbiddenException;
import com.example.TaskManager.exception.NotFoundException;
import com.example.TaskManager.model.Project;
import com.example.TaskManager.model.Role;
import com.example.TaskManager.model.TaskStatus;
import com.example.TaskManager.model.User;
import com.example.TaskManager.repository.ProjectRepository;
import com.example.TaskManager.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<ProjectResponse> findAccessibleProjects(User currentUser) {
        List<Project> projects = currentUser.getRole() == Role.ADMIN
                ? projectRepository.findAllByOrderByCreatedAtDesc()
                : projectRepository.findDistinctByMembersContainingOrderByCreatedAtDesc(currentUser);
        return projects.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request, User currentUser) {
        requireAdmin(currentUser);
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDueDate(request.dueDate());
        project.setOwner(currentUser);
        project.getMembers().add(currentUser);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long projectId, ProjectRequest request, User currentUser) {
        requireAdmin(currentUser);
        Project project = getProject(projectId);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDueDate(request.dueDate());
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse addMember(Long projectId, Long userId, User currentUser) {
        requireAdmin(currentUser);
        Project project = getProject(projectId);
        User member = userService.findById(userId);
        project.getMembers().add(member);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse removeMember(Long projectId, Long userId, User currentUser) {
        requireAdmin(currentUser);
        Project project = getProject(projectId);
        project.getMembers().removeIf(member -> member.getId().equals(userId));
        return toResponse(projectRepository.save(project));
    }

    public Project getAccessibleProject(Long projectId, User currentUser) {
        Project project = getProject(projectId);
        if (currentUser.getRole() == Role.ADMIN || project.getMembers().stream().anyMatch(user -> user.getId().equals(currentUser.getId()))) {
            return project;
        }
        throw new ForbiddenException("You do not have access to this project");
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private ProjectResponse toResponse(Project project) {
        List<com.example.TaskManager.model.Task> tasks = taskRepository.findByProjectOrderByCreatedAtDesc(project);
        long completed = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDueDate(),
                project.getCreatedAt(),
                userService.toSummary(project.getOwner()),
                new ArrayList<>(project.getMembers()).stream().map(userService::toSummary).toList(),
                tasks.size(),
                completed
        );
    }

    private void requireAdmin(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Admin access required");
        }
    }
}
