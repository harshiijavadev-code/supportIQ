package com.example.supportiq.repository;

import com.example.supportiq.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOrganizationId(Long organizationId);

    List<Ticket> findByAssignedToId(Long userId);
}
