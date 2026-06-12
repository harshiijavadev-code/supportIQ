package com.example.supportiq.Service;

import com.example.supportiq.dto.CreateTicketRequest;
import com.example.supportiq.entity.*;
import com.example.supportiq.entity.enums.TicketStatus;
import com.example.supportiq.entity.enums.TicketPriority;
import com.example.supportiq.entity.enums.UserRole;
import com.example.supportiq.exception.ResourceNotFoundException;
import com.example.supportiq.exception.UnauthorizedException;
import com.example.supportiq.repository.TicketRepository;
import com.example.supportiq.repository.UserRepository;
import com.example.supportiq.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AIService aiService;
    private final EmailService emailService;

    // ========== CREATE TICKET WITH AI PROCESSING ==========

    @Transactional
    public Ticket createTicket(CreateTicketRequest request) {
        try {
            // Get current user
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Get organization
            Organization organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

            // Create ticket
            Ticket ticket = Ticket.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .priority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM)
                    .status(TicketStatus.OPEN)
                    .createdByUser(currentUser)
                    .organization(organization)
                    .build();

            // Save initial ticket
            Ticket savedTicket = ticketRepository.save(ticket);

            // ========== AI PROCESSING ==========

            try {
                // 1. Categorize ticket
                log.info("Categorizing ticket {}", savedTicket.getId());
                String category = aiService.categorizeTicket(request.getTitle(), request.getDescription());
                savedTicket.setCategory(category);

                // 2. Analyze sentiment
                log.info("Analyzing sentiment for ticket {}", savedTicket.getId());
                AIService.SentimentResult sentiment = aiService.analyzeSentiment(request.getDescription());
                savedTicket.setSentiment(sentiment.getSentiment());
                savedTicket.setSentimentScore(sentiment.getScore());

                // 3. Auto-escalate ANGRY tickets
                if ("ANGRY".equalsIgnoreCase(sentiment.getSentiment())) {
                    savedTicket.setPriority(TicketPriority.HIGH);
                    savedTicket.setIsUrgent(true);
                    log.warn("Ticket {} marked as URGENT due to ANGRY sentiment", savedTicket.getId());
                }

                // 4. Generate response suggestion
                log.info("Generating response suggestion for ticket {}", savedTicket.getId());
                String suggestedResponse = aiService.suggestResponse(
                        category,
                        sentiment.getSentiment(),
                        request.getDescription()
                );
                savedTicket.setAiSuggestedResponse(suggestedResponse);

            } catch (Exception e) {
                log.error("Error processing AI features for ticket {}", savedTicket.getId(), e);
                savedTicket.setCategory("UNCATEGORIZED");
                savedTicket.setSentiment("NEUTRAL");
                savedTicket.setSentimentScore(0.5);
            }

            // Save ticket with AI data
            Ticket finalTicket = ticketRepository.save(savedTicket);

            // Send notification email
            try {
                emailService.sendTicketCreationEmail(currentUser.getEmail(), finalTicket);
            } catch (Exception e) {
                log.error("Error sending email notification", e);
            }

            log.info("Ticket created successfully: {} with category: {}, sentiment: {}",
                    finalTicket.getId(), finalTicket.getCategory(), finalTicket.getSentiment());

            return finalTicket;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating ticket", e);
            throw new RuntimeException("Error creating ticket: " + e.getMessage());
        }
    }

    // ========== GET ALL TICKETS ==========

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // ========== GET TICKETS BY ORGANIZATION ==========

    public List<Ticket> getTicketsByOrganization(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getOrganization().getId().equals(organizationId))
                .toList();
    }

    // ========== GET TICKET BY ID ==========

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    // ========== ASSIGN TICKET ==========

    @Transactional
    public Ticket assignTicket(Long ticketId, Long agentId) {
        Ticket ticket = getTicketById(ticketId);
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (agent.getRole() != UserRole.AGENT && agent.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("User is not an agent");
        }

        ticket.setAssignedToUser(agent);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Send assignment notification
        try {
            emailService.sendTicketAssignmentEmail(agent.getEmail(), savedTicket);
        } catch (Exception e) {
            log.error("Error sending assignment email", e);
        }

        return savedTicket;
    }

    // ========== UPDATE TICKET STATUS ==========

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus status) {
        Ticket ticket = getTicketById(ticketId);
        String previousStatus = ticket.getStatus().toString();
        ticket.setStatus(status);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Send status change notification
        try {
            emailService.sendTicketStatusChangeEmail(
                    ticket.getCreatedByUser().getEmail(),
                    savedTicket,
                    previousStatus
            );
        } catch (Exception e) {
            log.error("Error sending status change email", e);
        }

        return savedTicket;
    }

    // ========== UPDATE TICKET PRIORITY ==========

    @Transactional
    public Ticket updateTicketPriority(Long ticketId, TicketPriority priority) {
        Ticket ticket = getTicketById(ticketId);
        ticket.setPriority(priority);
        return ticketRepository.save(ticket);
    }

    // ========== DELETE TICKET ==========

    @Transactional
    public void deleteTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        ticketRepository.delete(ticket);
    }

    // ========== GET TICKETS BY STATUS ==========

    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getStatus() == status)
                .toList();
    }

    // ========== GET TICKETS BY PRIORITY ==========

    public List<Ticket> getTicketsByPriority(TicketPriority priority) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getPriority() == priority)
                .toList();
    }
}