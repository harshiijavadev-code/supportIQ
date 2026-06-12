package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateTicketRequest;
import com.example.supportiq.entity.Ticket;
import com.example.supportiq.entity.enums.TicketStatus;
import com.example.supportiq.entity.enums.TicketPriority;
import com.example.supportiq.Service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ========== CREATE TICKET ==========

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        log.info("Creating ticket: {}", request.getTitle());
        Ticket ticket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    // ========== GET ALL TICKETS ==========

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        log.info("Fetching all tickets");
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    // ========== GET TICKETS BY ORGANIZATION ==========

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsByOrganization(@PathVariable Long organizationId) {
        log.info("Fetching tickets for organization: {}", organizationId);
        List<Ticket> tickets = ticketService.getTicketsByOrganization(organizationId);
        return ResponseEntity.ok(tickets);
    }

    // ========== GET TICKET BY ID ==========

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        log.info("Fetching ticket: {}", id);
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    // ========== ASSIGN TICKET ==========

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Ticket> assignTicket(
            @PathVariable Long id,
            @RequestParam Long agentId) {
        log.info("Assigning ticket {} to agent {}", id, agentId);
        Ticket ticket = ticketService.assignTicket(id, agentId);
        return ResponseEntity.ok(ticket);
    }

    // ========== UPDATE TICKET STATUS ==========

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        log.info("Updating ticket {} status to {}", id, status);
        Ticket ticket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(ticket);
    }

    // ========== UPDATE TICKET PRIORITY ==========

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Ticket> updateTicketPriority(
            @PathVariable Long id,
            @RequestParam TicketPriority priority) {
        log.info("Updating ticket {} priority to {}", id, priority);
        Ticket ticket = ticketService.updateTicketPriority(id, priority);
        return ResponseEntity.ok(ticket);
    }

    // ========== DELETE TICKET ==========

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        log.info("Deleting ticket: {}", id);
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    // ========== GET TICKETS BY STATUS ==========

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable TicketStatus status) {
        log.info("Fetching tickets with status: {}", status);
        List<Ticket> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    // ========== GET TICKETS BY PRIORITY ==========

    @GetMapping("/priority/{priority}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsByPriority(@PathVariable TicketPriority priority) {
        log.info("Fetching tickets with priority: {}", priority);
        List<Ticket> tickets = ticketService.getTicketsByPriority(priority);
        return ResponseEntity.ok(tickets);
    }
}