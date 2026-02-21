package com.example.supportiq.service;
import com.example.supportiq.exception.ResourceNotFoundException;
import com.example.supportiq.entity.Ticket;
import com.example.supportiq.entity.TicketComment;
import com.example.supportiq.entity.User;
import com.example.supportiq.repository.TicketCommentRepository;
import com.example.supportiq.repository.TicketRepository;
import com.example.supportiq.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketCommentService(TicketCommentRepository ticketCommentRepository,
                                TicketRepository ticketRepository,
                                UserRepository userRepository) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Add a comment to a ticket
     */
    public TicketComment addComment(Long ticketId, Long userId, String message) {
        // Verify ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Create the comment
        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setUser(user);
        comment.setMessage(message);

        return ticketCommentRepository.save(comment);
    }

    /**
     * Get all comments for a specific ticket
     */
    public List<TicketComment> getCommentsByTicket(Long ticketId) {
        return ticketCommentRepository.findByTicketId(ticketId);
    }

    /**
     * Delete a comment
     */
    public void deleteComment(Long commentId) {
        if (!ticketCommentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment", commentId);
        }
        ticketCommentRepository.deleteById(commentId);
    }
}