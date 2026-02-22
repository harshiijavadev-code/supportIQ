package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateTicketRequest;
import com.example.supportiq.dto.UpdateTicketStatusRequest;
import com.example.supportiq.dto.AssignTicketRequest;
import com.example.supportiq.entity.Ticket;
import com.example.supportiq.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
     * Create a new ticket
     * POST /api/tickets
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket createTicket( @Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getCreatedByUserId(),
                request.getOrganizationId()
        );
    }

    /**
     * Get all tickets (optionally filtered by organization)
     * GET /api/tickets?organizationId=1
     */
    @GetMapping
    public List<Ticket> getAllTickets(@RequestParam(required = false) Long organizationId) {
        if (organizationId != null) {
            return ticketService.getTicketsByOrganization(organizationId);
        }
        return ticketService.getAllTickets();
    }

    /**
     * Get a specific ticket by ID
     * GET /api/tickets/1
     */
    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    /**
     * Assign ticket to an agent
     * PUT /api/tickets/1/assign
     */
    @PutMapping("/{id}/assign")
    public Ticket assignTicket(@PathVariable Long id, @RequestBody AssignTicketRequest request) {
        return ticketService.assignTicket(id, request.getAgentId());
    }

    /**
     * Update ticket status
     * PUT /api/tickets/1/status
     */
    @PutMapping("/{id}/status")
    public Ticket updateTicketStatus(@PathVariable Long id, @RequestBody UpdateTicketStatusRequest request) {
        return ticketService.updateTicketStatus(id, request.getStatus());
    }

    /**
     * Delete a ticket (soft delete recommended in production)
     * DELETE /api/tickets/1
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
}
