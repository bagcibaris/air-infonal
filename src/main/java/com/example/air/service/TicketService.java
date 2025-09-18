package com.example.air.service;

import com.example.air.domain.Flight;
import com.example.air.domain.Ticket;
import com.example.air.domain.TicketStatus;
import com.example.air.exception.FlightFullException;
import com.example.air.exception.badrequest.CardNumberRequiredException;
import com.example.air.exception.badrequest.FlightIdRequiredException;
import com.example.air.exception.badrequest.PassengerNameRequiredException;
import com.example.air.exception.badrequest.TicketNumberRequiredException;
import com.example.air.exception.notfound.FlightNotFoundException;
import com.example.air.exception.notfound.TicketNumberNotFoundException;
import com.example.air.repo.FlightRepository;
import com.example.air.repo.TicketRepository;
import com.example.air.util.CardMasker;
import com.example.air.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;

    @Transactional
    public Ticket purchase(Long flightId, String passengerName, String passengerEmail, String rawCardNumber) {
        if (flightId == null) throw new FlightIdRequiredException();
        if (passengerName == null || passengerName.trim().isEmpty())
            throw new PassengerNameRequiredException();
        if (rawCardNumber == null || rawCardNumber.trim().isEmpty())
            throw new CardNumberRequiredException();

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        if (flight.getSeatsSold() >= flight.getCapacity()) {
            throw new FlightFullException(flightId);
        }

        BigDecimal price = PriceCalculator.priceForNextSeat(
                flight.getBasePrice(), flight.getCapacity(), flight.getSeatsSold()
        );

        flight.setSeatsSold(flight.getSeatsSold() + 1);
        flightRepository.save(flight);

        String masked = CardMasker.mask(rawCardNumber);

        Ticket ticket = Ticket.builder()
                .flight(flight)
                .ticketNumber(generateTicketNumber(flight.getId()))
                .passengerName(passengerName)
                .passengerEmail(passengerEmail)
                .pricePaid(price.doubleValue())
                .maskedCardNumber(masked)
                .status(TicketStatus.ACTIVE)
                .purchasedAt(LocalDateTime.now())
                .build();

        return ticketRepository.save(ticket);
    }

    public Ticket findByNumber(String ticketNumber) {
        if (ticketNumber == null || ticketNumber.trim().isEmpty()) {
            throw new TicketNumberRequiredException();
        }
        return ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new TicketNumberNotFoundException(ticketNumber));
    }

    @Transactional
    public Ticket cancel(String ticketNumber) {
        Ticket t = findByNumber(ticketNumber);
        if (t.getStatus() == TicketStatus.CANCELLED) {
            return t;
        }

        t.setStatus(TicketStatus.CANCELLED);

        Flight f = t.getFlight();
        f.setSeatsSold(Math.max(0, f.getSeatsSold() - 1));
        flightRepository.save(f);

        return ticketRepository.save(t);
    }

    private String generateTicketNumber(Long flightId) {
        return "FL-" + flightId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
