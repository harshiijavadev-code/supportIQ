package com.example.supportiq.entity;

import com.example.supportiq.entity.enums.TicketStatus;
import com.example.supportiq.entity.enums.TicketPriority;


import com.example.supportiq.entity.enums.TicketPriority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedToUser;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketComment> comments = new ArrayList<>();

    // ========== NEW AI FIELDS ==========

    @Column(name = "category")
    private String category; // TECHNICAL, BILLING, FEATURE_REQUEST, BUG, UNCATEGORIZED

    @Column(name = "sentiment")
    private String sentiment;

    @Column(name = "sentiment_score")
    private Double sentimentScore;

    @Column(columnDefinition = "TEXT", name = "ai_suggested_response")
    private String aiSuggestedResponse; // AI-generated response draft

    @Column(name = "is_urgent")
    private Boolean isUrgent = false; // For ANGRY sentiment auto-escalation

    // ========== AUDIT FIELDS ==========

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ========== VERSION FOR OPTIMISTIC LOCKING ==========

    @Version
    private Integer version;
}