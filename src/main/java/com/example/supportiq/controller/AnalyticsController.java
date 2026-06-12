package com.example.supportiq.controller;

import com.example.supportiq.dto.DashboardResponse;
import com.example.supportiq.Service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get manager dashboard with statistics
     * Only ADMIN and AGENT can access
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<DashboardResponse> getDashboard() {
        log.info("Dashboard requested");
        DashboardResponse dashboard = analyticsService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get ticket statistics summary
     */
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getSummary() {
        log.info("Summary requested");
        DashboardResponse dashboard = analyticsService.getDashboard();
        String summary = String.format("""
                SupportIQ Analytics Summary
                ============================
                Total Tickets: %d
                Open Tickets: %d
                In Progress: %d
                Resolved: %d
                Closed: %d
                High Priority: %d
                Urgent: %d
                Average Resolution Time: %.1f hours
                """,
                dashboard.getTotalTickets(),
                dashboard.getOpenTickets(),
                dashboard.getInProgressTickets(),
                dashboard.getResolvedTickets(),
                dashboard.getClosedTickets(),
                dashboard.getHighPriorityTickets(),
                dashboard.getUrgentTickets(),
                dashboard.getAverageResolutionTime()
        );
        return ResponseEntity.ok(summary);
    }
}