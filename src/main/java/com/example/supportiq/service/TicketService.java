package com.example.supportiq.service;

import com.example.supportiq.entity.Organization;
import com.example.supportiq.entity.Ticket;
import com.example.supportiq.entity.User;
import com.example.supportiq.entity.enums.TicketPriority;
import com.example.supportiq.entity.enums.TicketStatus;
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

    public List<Ticket> getTicketsByOrganization(Long organizationId) {
        return ticketRepository.findByOrganizationId(organizationId);
    }
}

