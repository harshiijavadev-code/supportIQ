package com.example.supportiq.service;

import com.example.supportiq.entity.Organization;
import com.example.supportiq.entity.Ticket;
import com.example.supportiq.entity.User;
import com.example.supportiq.entity.enums.TicketPriority;
import com.example.supportiq.entity.enums.TicketStatus;
import com.example.supportiq.entity.enums.UserRole;
import com.example.supportiq.repository.OrganizationRepository;
import com.example.supportiq.repository.TicketRepository;
import com.example.supportiq.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         OrganizationRepository organizationRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Create a new ticket
     */
    public Ticket createTicket(String title,
                               String description,
                               TicketPriority priority,
                               Long createdByUserId,
                               Long organizationId) {

        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(createdBy);
        ticket.setOrganization(organization);

        return ticketRepository.save(ticket);
    }

    /**
     * Get all tickets in the system
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Get tickets by organization
     */
    public List<Ticket> getTicketsByOrganization(Long organizationId) {
        return ticketRepository.findByOrganizationId(organizationId);
    }

    /**
     * Get a specific ticket by ID
     */
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    /**
     * Assign a ticket to an agent
     */
    public Ticket assignTicket(Long ticketId, Long agentId) {
        Ticket ticket = getTicketById(ticketId);
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // Verify the user is actually an AGENT (FIXED COMPARISON)
        if (!UserRole.AGENT.equals(agent.getRole())) {
            throw new RuntimeException("User is not an agent. Only users with AGENT role can be assigned tickets.");
        }

        ticket.setAssignedTo(agent);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    /**
     * Update ticket status
     */
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = getTicketById(ticketId);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    /**
     * Delete a ticket
     */
    public void deleteTicket(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new RuntimeException("Ticket not found with id: " + ticketId);
        }
        ticketRepository.deleteById(ticketId);
    }
}