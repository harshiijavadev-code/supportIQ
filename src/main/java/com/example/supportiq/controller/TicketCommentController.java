package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateCommentRequest;
import com.example.supportiq.entity.TicketComment;
import com.example.supportiq.service.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;

    public TicketCommentController(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    /**
     * Add comment - All authenticated users
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public TicketComment addComment(@PathVariable Long ticketId, @Valid @RequestBody CreateCommentRequest request) {
        return ticketCommentService.addComment(ticketId, request.getUserId(), request.getMessage());
    }

    /**
     * Get comments - All authenticated users
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TicketComment> getComments(@PathVariable Long ticketId) {
        return ticketCommentService.getCommentsByTicket(ticketId);
    }

    /**
     * Delete comment - Only Admins
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteComment(@PathVariable Long ticketId, @PathVariable Long commentId) {
        ticketCommentService.deleteComment(commentId);
    }
}