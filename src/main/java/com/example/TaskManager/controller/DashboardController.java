package com.example.TaskManager.controller;

import com.example.TaskManager.dto.DashboardResponse;
import com.example.TaskManager.security.AppUserPrincipal;
import com.example.TaskManager.service.CurrentUserService;
import com.example.TaskManager.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public DashboardController(DashboardService dashboardService, CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public DashboardResponse getDashboard(@AuthenticationPrincipal AppUserPrincipal principal) {
        return dashboardService.getDashboard(currentUserService.fromPrincipal(principal));
    }
}
