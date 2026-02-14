package com.example.supportiq.dto;

import com.example.supportiq.entity.enums.TicketStatus;

public class UpdateTicketStatusRequest {

    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}