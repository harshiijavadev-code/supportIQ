package com.example.supportiq.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private Long totalTickets;  // ← Should be Long
    private Long openTickets;
    private Long inProgressTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Long highPriorityTickets;
    private Long urgentTickets;
    private Double averageResolutionTime; // in hours
    private Map<String, Long> sentimentDistribution;
    private Map<String, Long> categoryDistribution;
    private List<AgentPerformance> agentPerformance;
    private List<RecurringIssue> recurringIssues;
    private LocalDateTime generatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgentPerformance {
        private String agentName;
        private String agentEmail;
        private Long assignedTickets;     // ← Should be Long
        private Long resolvedTickets;     // ← Should be Long
        private Long pendingTickets;      // ← Should be Long
        private Double averageResolutionHours;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecurringIssue {
        private String category;
        private Long occurrenceCount;     // ← Should be Long
        private Double percentage;
    }
}