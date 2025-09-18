package com.example.air.exception.badrequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketNumberRequiredException extends RuntimeException {
    public TicketNumberRequiredException() {
        super("ticketNumber is required");
    }
}
