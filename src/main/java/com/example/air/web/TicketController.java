package com.example.air.web;

import com.example.air.domain.Ticket;
import com.example.air.service.TicketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    @Data
    public static class PurchaseRequest {
        private Long flightId;
        private String passengerName;
        private String passengerEmail;
        private String cardNumber;
    }

    @PostMapping("/purchase")
    public Ticket purchase(@RequestBody PurchaseRequest req) {
        return service.purchase(
                req.getFlightId(),
                req.getPassengerName(),
                req.getPassengerEmail(),
                req.getCardNumber()
        );
    }

    @GetMapping("/{ticketNumber}")
    public Ticket get(@PathVariable String ticketNumber) {
        return service.findByNumber(ticketNumber);
    }

    @PostMapping("/{ticketNumber}/cancel")
    public Ticket cancel(@PathVariable String ticketNumber) {
        return service.cancel(ticketNumber);
    }
}
