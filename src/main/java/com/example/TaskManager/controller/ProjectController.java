package com.example.TaskManager.controller;

import com.example.TaskManager.dto.ProjectRequest;
import com.example.TaskManager.dto.ProjectResponse;
import com.example.TaskManager.security.AppUserPrincipal;
import com.example.TaskManager.service.CurrentUserService;
import com.example.TaskManager.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final CurrentUserService currentUserService;

    public ProjectController(ProjectService projectService, CurrentUserService currentUserService) {
        this.projectService = projectService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<ProjectResponse> findAll(@AuthenticationPrincipal AppUserPrincipal principal) {
        return projectService.findAccessibleProjects(currentUserService.fromPrincipal(principal));
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request,
                                  @AuthenticationPrincipal AppUserPrincipal principal) {
        return projectService.create(request, currentUserService.fromPrincipal(principal));
    }

    @PutMapping("/{projectId}")
    public ProjectResponse update(@PathVariable Long projectId,
                                  @Valid @RequestBody ProjectRequest request,
                                  @AuthenticationPrincipal AppUserPrincipal principal) {
        return projectService.update(projectId, request, currentUserService.fromPrincipal(principal));
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ProjectResponse addMember(@PathVariable Long projectId,
                                     @PathVariable Long userId,
                                     @AuthenticationPrincipal AppUserPrincipal principal) {
        return projectService.addMember(projectId, userId, currentUserService.fromPrincipal(principal));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ProjectResponse removeMember(@PathVariable Long projectId,
                                        @PathVariable Long userId,
                                        @AuthenticationPrincipal AppUserPrincipal principal) {
        return projectService.removeMember(projectId, userId, currentUserService.fromPrincipal(principal));
    }
}
