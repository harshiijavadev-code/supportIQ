package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateTicketRequest;
import com.example.supportiq.dto.UpdateTicketStatusRequest;
import com.example.supportiq.dto.AssignTicketRequest;
import com.example.supportiq.entity.Ticket;
import com.example.supportiq.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Create ticket - All authenticated users can create tickets
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public Ticket createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getCreatedByUserId(),
                request.getOrganizationId()
        );
    }

    /**
     * Get all tickets - Agents and Admins only
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public List<Ticket> getAllTickets(@RequestParam(required = false) Long organizationId) {
        if (organizationId != null) {
            return ticketService.getTicketsByOrganization(organizationId);
        }
        return ticketService.getAllTickets();
    }

    /**
     * Get ticket by ID - All authenticated users
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    /**
     * Assign ticket - Only Agents and Admins
     */
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public Ticket assignTicket(@PathVariable Long id, @Valid @RequestBody AssignTicketRequest request) {
        return ticketService.assignTicket(id, request.getAgentId());
    }

    /**
     * Update ticket status - Only Agents and Admins
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public Ticket updateTicketStatus(@PathVariable Long id, @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ticketService.updateTicketStatus(id, request.getStatus());
    }

    /**
     * Delete ticket - Only Admins
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
}