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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    private TicketRepository ticketRepo;
    private FlightRepository flightRepo;
    private TicketService service;

    @BeforeEach
    void setUp() {
        ticketRepo = mock(TicketRepository.class);
        flightRepo  = mock(FlightRepository.class);
        service = new TicketService(ticketRepo, flightRepo);
    }

    private Flight flight(int capacity, int sold, BigDecimal base) {
        Flight f = new Flight();
        f.setId(1L);
        f.setCapacity(capacity);
        f.setSeatsSold(sold);
        f.setBasePrice(base);
        f.setDepartureTime(LocalDateTime.now().plusDays(1));
        f.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        return f;
    }


    @Test
    void purchase_happyPath_setsFields_masksCard_incrementsSeats() {
        Flight f = flight(10, 0, new BigDecimal("100"));
        when(flightRepo.findById(1L)).thenReturn(Optional.of(f));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(flightRepo.save(any(Flight.class))).thenAnswer(i -> i.getArgument(0));

        Ticket t = service.purchase(1L, "Ali Veli", "ali@example.com", "4221-1611-2233-0005");

        assertEquals(100.0, t.getPricePaid());                           // ilk %10 baz fiyat
        assertTrue(t.getMaskedCardNumber().matches("^\\d{6}\\*{6}\\d{4}$"), "mask format");
        assertEquals(TicketStatus.ACTIVE, t.getStatus());
        assertNotNull(t.getPurchasedAt());
        assertTrue(t.getTicketNumber().startsWith("FL-1-"), "ticket number prefix");
        assertEquals(1, f.getSeatsSold());                               // +1
        verify(flightRepo).save(f);
        verify(ticketRepo).save(any(Ticket.class));
    }

    @Test
    void purchase_thresholdPricing_10PercentBlock_applies() {
        // seatsSold = 10 ⇒ %10-19 bandı ⇒ +%10
        Flight f = flight(100, 10, new BigDecimal("200.00"));
        when(flightRepo.findById(1L)).thenReturn(Optional.of(f));
        when(flightRepo.save(any(Flight.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket t = service.purchase(1L, "Ayşe", "ayse@example.com", "4111 1111 1111 1111");

        assertEquals(220.00, t.getPricePaid(), 0.0001);
        assertEquals(11, f.getSeatsSold());
    }

    @Test
    void purchase_whenFlightIsFull_throwsFlightFullException() {
        Flight f = flight(10, 10, new BigDecimal("100"));
        when(flightRepo.findById(1L)).thenReturn(Optional.of(f));

        assertThrows(FlightFullException.class,
                () -> service.purchase(1L, "Ali", null, "4111 1111 1111 1111"));
        verify(ticketRepo, never()).save(any());
        verify(flightRepo, never()).save(any());
    }

    @Test
    void purchase_whenFlightNotFound_throwsFlightNotFoundException() {
        when(flightRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class,
                () -> service.purchase(999L, "Ali", "a@b.com", "4111111111111111"));
    }

    @Test
    void purchase_validation_throwsSpecificBadRequestExceptions() {
        assertThrows(FlightIdRequiredException.class,
                () -> service.purchase(null, "A", "a@b.com", "4111"));

        assertThrows(PassengerNameRequiredException.class,
                () -> service.purchase(1L, null, "a@b.com", "4111"));
        assertThrows(PassengerNameRequiredException.class,
                () -> service.purchase(1L, "   ", "a@b.com", "4111"));

        assertThrows(CardNumberRequiredException.class,
                () -> service.purchase(1L, "A", "a@b.com", null));
        assertThrows(CardNumberRequiredException.class,
                () -> service.purchase(1L, "A", "a@b.com", "   "));
    }


    @Test
    void findByNumber_returnsTicket_whenExists() {
        Ticket existing = Ticket.builder()
                .id(10L).ticketNumber("FL-1-ABCDEFGH")
                .status(TicketStatus.ACTIVE).build();
        when(ticketRepo.findByTicketNumber("FL-1-ABCDEFGH")).thenReturn(Optional.of(existing));

        Ticket t = service.findByNumber("FL-1-ABCDEFGH");

        assertEquals(10L, t.getId());
        verify(ticketRepo).findByTicketNumber("FL-1-ABCDEFGH");
    }

    @Test
    void findByNumber_required_validations() {
        assertThrows(TicketNumberRequiredException.class, () -> service.findByNumber(null));
        assertThrows(TicketNumberRequiredException.class, () -> service.findByNumber("   "));
        verify(ticketRepo, never()).findByTicketNumber(anyString());
    }

    @Test
    void findByNumber_notFound_throwsTicketNumberNotFoundException() {
        when(ticketRepo.findByTicketNumber("X")).thenReturn(Optional.empty());

        assertThrows(TicketNumberNotFoundException.class,
                () -> service.findByNumber("X"));
    }


    @Test
    void cancel_activeTicket_setsCancelled_andDecrementsSeats_andSaves() {
        Flight f = flight(10, 5, new BigDecimal("100"));
        Ticket existing = Ticket.builder()
                .id(99L).flight(f).ticketNumber("X")
                .passengerName("Ali").status(TicketStatus.ACTIVE).build();

        when(ticketRepo.findByTicketNumber("X")).thenReturn(Optional.of(existing));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(flightRepo.save(any(Flight.class))).thenAnswer(i -> i.getArgument(0));

        Ticket cancelled = service.cancel("X");

        assertEquals(TicketStatus.CANCELLED, cancelled.getStatus());
        assertEquals(4, f.getSeatsSold());
        verify(flightRepo).save(f);
        verify(ticketRepo).save(existing);
    }

    @Test
    void cancel_whenAlreadyCancelled_returnsAsIs_andDoesNotSave() {
        Flight f = flight(10, 7, new BigDecimal("100"));
        Ticket existing = Ticket.builder()
                .id(101L).flight(f).ticketNumber("Y")
                .passengerName("Veli").status(TicketStatus.CANCELLED).build();

        when(ticketRepo.findByTicketNumber("Y")).thenReturn(Optional.of(existing));

        Ticket result = service.cancel("Y");

        assertSame(existing, result);
        assertEquals(7, f.getSeatsSold()); // değişmedi
        verify(flightRepo, never()).save(any());
        verify(ticketRepo, never()).save(any(Ticket.class));
    }

    @Test
    void cancel_whenSeatsSoldZero_doesNotUnderflow() {
        Flight f = flight(10, 0, new BigDecimal("100"));
        Ticket existing = Ticket.builder()
                .id(55L).flight(f).ticketNumber("Z")
                .passengerName("Ahmet").status(TicketStatus.ACTIVE).build();

        when(ticketRepo.findByTicketNumber("Z")).thenReturn(Optional.of(existing));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(flightRepo.save(any(Flight.class))).thenAnswer(i -> i.getArgument(0));

        Ticket cancelled = service.cancel("Z");

        assertEquals(TicketStatus.CANCELLED, cancelled.getStatus());
        assertEquals(0, f.getSeatsSold()); // Math.max(0, -1) → 0
    }
}
