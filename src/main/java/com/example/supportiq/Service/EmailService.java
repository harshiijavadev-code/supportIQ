package com.example.supportiq.Service;

import com.example.supportiq.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "noreply@supportiq.com";

    /**
     * Send email when ticket is created
     */
    public void sendTicketCreationEmail(String customerEmail, Ticket ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(customerEmail);
            message.setSubject("Ticket Created - #" + ticket.getId() + ": " + ticket.getTitle());
            message.setText(String.format("""
                    Dear Customer,
                    
                    Thank you for contacting us. Your support ticket has been created successfully.
                    
                    Ticket Details:
                    - Ticket ID: #%d
                    - Title: %s
                    - Status: %s
                    - Priority: %s
                    - Category: %s
                    
                    Our team will get back to you soon.
                    
                    Best regards,
                    SupportIQ Team
                    """,
                    ticket.getId(),
                    ticket.getTitle(),
                    ticket.getStatus(),
                    ticket.getPriority(),
                    ticket.getCategory()
            ));

            mailSender.send(message);
            log.info("Ticket creation email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Error sending ticket creation email", e);
        }
    }

    /**
     * Send email when ticket is assigned
     */
    public void sendTicketAssignmentEmail(String agentEmail, Ticket ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(agentEmail);
            message.setSubject("Ticket Assigned - #" + ticket.getId() + ": " + ticket.getTitle());
            message.setText(String.format("""
                    Dear Agent,
                    
                    A new ticket has been assigned to you.
                    
                    Ticket Details:
                    - Ticket ID: #%d
                    - Title: %s
                    - Priority: %s
                    - Sentiment: %s (Score: %.2f)
                    - Category: %s
                    - Urgent: %s
                    
                    Suggested Response:
                    %s
                    
                    Please handle this ticket accordingly.
                    
                    Best regards,
                    SupportIQ System
                    """,
                    ticket.getId(),
                    ticket.getTitle(),
                    ticket.getPriority(),
                    ticket.getSentiment(),
                    ticket.getSentimentScore() != null ? ticket.getSentimentScore() : 0.0,
                    ticket.getCategory(),
                    ticket.getIsUrgent() != null && ticket.getIsUrgent() ? "YES" : "NO",
                    ticket.getAiSuggestedResponse() != null ? ticket.getAiSuggestedResponse() : "N/A"
            ));

            mailSender.send(message);
            log.info("Ticket assignment email sent to: {}", agentEmail);

        } catch (Exception e) {
            log.error("Error sending ticket assignment email", e);
        }
    }

    /**
     * Send email when ticket status changes
     */
    public void sendTicketStatusChangeEmail(String customerEmail, Ticket ticket, String previousStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(customerEmail);
            message.setSubject("Ticket Status Updated - #" + ticket.getId() + ": " + ticket.getTitle());
            message.setText(String.format("""
                    Dear Customer,
                    
                    Your support ticket status has been updated.
                    
                    Ticket Details:
                    - Ticket ID: #%d
                    - Title: %s
                    - Previous Status: %s
                    - Current Status: %s
                    
                    We will continue to work on resolving your issue.
                    
                    Best regards,
                    SupportIQ Team
                    """,
                    ticket.getId(),
                    ticket.getTitle(),
                    previousStatus,
                    ticket.getStatus()
            ));

            mailSender.send(message);
            log.info("Ticket status change email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Error sending ticket status change email", e);
        }
    }
}