package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateCommentRequest;
import com.example.supportiq.entity.TicketComment;
import com.example.supportiq.service.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
     * Add a comment to a ticket
     * POST /api/tickets/1/comments
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketComment addComment(@PathVariable Long ticketId,@Valid @RequestBody CreateCommentRequest request) {
        return ticketCommentService.addComment(ticketId, request.getUserId(), request.getMessage());
    }

    /**
     * Get all comments for a ticket
     * GET /api/tickets/1/comments
     */
    @GetMapping
    public List<TicketComment> getComments(@PathVariable Long ticketId) {
        return ticketCommentService.getCommentsByTicket(ticketId);
    }

    /**
     * Delete a comment
     * DELETE /api/tickets/1/comments/5
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long ticketId, @PathVariable Long commentId) {
        ticketCommentService.deleteComment(commentId);
    }
}
