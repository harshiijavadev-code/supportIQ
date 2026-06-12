package com.example.supportiq.Service;

import com.example.supportiq.dto.DashboardResponse;
import com.example.supportiq.entity.*;
import com.example.supportiq.repository.TicketRepository;
import com.example.supportiq.repository.UserRepository;
import com.example.supportiq.entity.enums.TicketStatus;
import com.example.supportiq.entity.enums.TicketPriority;
import com.example.supportiq.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    /**
     * Get manager dashboard with all statistics
     */
    public DashboardResponse getDashboard() {
        try {
            List<Ticket> allTickets = ticketRepository.findAll();
            List<User> agents = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.AGENT)
                    .toList();

            DashboardResponse dashboard = DashboardResponse.builder()
                    .totalTickets((long) allTickets.size())  // Convert to Long
                    .openTickets(countByStatus(allTickets, TicketStatus.OPEN))
                    .inProgressTickets(countByStatus(allTickets, TicketStatus.IN_PROGRESS))
                    .resolvedTickets(countByStatus(allTickets, TicketStatus.RESOLVED))
                    .closedTickets(countByStatus(allTickets, TicketStatus.CLOSED))
                    .highPriorityTickets(countByPriority(allTickets, TicketPriority.HIGH))
                    .averageResolutionTime(calculateAverageResolutionTime(allTickets))
                    .sentimentDistribution(calculateSentimentDistribution(allTickets))
                    .categoryDistribution(calculateCategoryDistribution(allTickets))
                    .agentPerformance(calculateAgentPerformance(agents, allTickets))
                    .recurringIssues(findRecurringIssues(allTickets))
                    .urgentTickets(countUrgentTickets(allTickets))
                    .generatedAt(LocalDateTime.now())
                    .build();

            log.info("Dashboard generated successfully");
            return dashboard;

        } catch (Exception e) {
            log.error("Error generating dashboard", e);
            return DashboardResponse.builder()
                    .totalTickets(0L)  // Use 0L for Long
                    .generatedAt(LocalDateTime.now())
                    .build();
        }
    }

    // ========== HELPER METHODS ==========

    private long countByStatus(List<Ticket> tickets, TicketStatus status) {
        return tickets.stream()
                .filter(t -> t.getStatus() == status)
                .count();
    }

    private long countByPriority(List<Ticket> tickets, TicketPriority priority) {
        return tickets.stream()
                .filter(t -> t.getPriority() == priority)
                .count();
    }

    private double calculateAverageResolutionTime(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED)
                .mapToLong(t -> ChronoUnit.HOURS.between(t.getCreatedAt(), t.getUpdatedAt()))
                .average()
                .orElse(0.0);
    }

    private Map<String, Long> calculateSentimentDistribution(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getSentiment() != null)
                .collect(Collectors.groupingBy(
                        Ticket::getSentiment,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> calculateCategoryDistribution(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Ticket::getCategory,
                        Collectors.counting()
                ));
    }

    private List<DashboardResponse.AgentPerformance> calculateAgentPerformance(
            List<User> agents, List<Ticket> allTickets) {

        return agents.stream()
                .map(agent -> {
                    List<Ticket> agentTickets = allTickets.stream()
                            .filter(t -> t.getAssignedToUser() != null &&
                                    agent.getId().equals(t.getAssignedToUser().getId()))
                            .toList();

                    long resolvedCount = agentTickets.stream()
                            .filter(t -> t.getStatus() == TicketStatus.RESOLVED
                                    || t.getStatus() == TicketStatus.CLOSED)
                            .count();

                    long pendingCount = agentTickets.size() - resolvedCount;

                    double avgResolutionTime = agentTickets.stream()
                            .filter(t -> t.getStatus() == TicketStatus.RESOLVED
                                    || t.getStatus() == TicketStatus.CLOSED)
                            .mapToLong(t -> ChronoUnit.HOURS.between(t.getCreatedAt(), t.getUpdatedAt()))
                            .average()
                            .orElse(0.0);

                    return DashboardResponse.AgentPerformance.builder()
                            .agentName(agent.getName())
                            .agentEmail(agent.getEmail())
                            .assignedTickets((long) agentTickets.size())  // Convert to Long
                            .resolvedTickets(resolvedCount)
                            .pendingTickets(pendingCount)  // Now it's Long
                            .averageResolutionHours(avgResolutionTime)
                            .build();
                })
                .toList();
    }

    private List<DashboardResponse.RecurringIssue> findRecurringIssues(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Long> categoryCount = calculateCategoryDistribution(tickets);

        return categoryCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> DashboardResponse.RecurringIssue.builder()
                        .category(entry.getKey())
                        .occurrenceCount(entry.getValue())
                        .percentage((entry.getValue() * 100.0) / tickets.size())
                        .build())
                .toList();
    }

    private long countUrgentTickets(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getIsUrgent() != null && t.getIsUrgent())
                .count();
    }
}