package com.example.TaskManager.controller;

import com.example.TaskManager.dto.TaskRequest;
import com.example.TaskManager.dto.TaskResponse;
import com.example.TaskManager.dto.TaskStatusUpdateRequest;
import com.example.TaskManager.security.AppUserPrincipal;
import com.example.TaskManager.service.CurrentUserService;
import com.example.TaskManager.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    public TaskController(TaskService taskService, CurrentUserService currentUserService) {
        this.taskService = taskService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<TaskResponse> findAll(@RequestParam(required = false) Long projectId,
                                      @AuthenticationPrincipal AppUserPrincipal principal) {
        return taskService.findTasks(projectId, currentUserService.fromPrincipal(principal));
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody TaskRequest request,
                               @AuthenticationPrincipal AppUserPrincipal principal) {
        return taskService.create(request, currentUserService.fromPrincipal(principal));
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(@PathVariable Long taskId,
                               @Valid @RequestBody TaskRequest request,
                               @AuthenticationPrincipal AppUserPrincipal principal) {
        return taskService.update(taskId, request, currentUserService.fromPrincipal(principal));
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateStatus(@PathVariable Long taskId,
                                     @Valid @RequestBody TaskStatusUpdateRequest request,
                                     @AuthenticationPrincipal AppUserPrincipal principal) {
        return taskService.updateStatus(taskId, request.status(), currentUserService.fromPrincipal(principal));
    }
}
