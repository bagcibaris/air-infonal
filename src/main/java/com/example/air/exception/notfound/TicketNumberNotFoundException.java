package com.example.air.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TicketNumberNotFoundException extends RuntimeException {
    public TicketNumberNotFoundException(String number) {
        super("Ticket with number=" + number + " not found");
    }
}
