package com.interviewprep.backend.controller;

import com.interviewprep.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress() {
        return ResponseEntity.ok(dashboardService.getTopicProgress());
    }

    @GetMapping("/activity")
    public ResponseEntity<Map<String, Object>> getActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFullDashboard() {
        return ResponseEntity.ok(dashboardService.getFullDashboard());
    }
}
