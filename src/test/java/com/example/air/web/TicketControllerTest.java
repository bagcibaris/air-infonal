package com.example.air.web;

import com.example.air.domain.Flight;
import com.example.air.domain.Ticket;
import com.example.air.domain.TicketStatus;
import com.example.air.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ticket sampleTicket() {
        Ticket t = Ticket.builder()
                .id(10L)
                .flight(new Flight())
                .ticketNumber("FL-1-ABCDEFGH")
                .passengerName("Ali Veli")
                .passengerEmail("ali@example.com")
                .pricePaid(110.00)
                .maskedCardNumber("422116******0005")
                .status(TicketStatus.ACTIVE)
                .purchasedAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();
        return t;
    }

    @Test
    void purchase_shouldReturnTicket() throws Exception {
        Ticket returned = sampleTicket();

        Mockito.when(ticketService.purchase(
                eq(1L), eq("Ali Veli"), eq("ali@example.com"), eq("4221-1611-2233-0005")
        )).thenReturn(returned);

        String body = "{\n" +
                "  \"flightId\": 1,\n" +
                "  \"passengerName\": \"Ali Veli\",\n" +
                "  \"passengerEmail\": \"ali@example.com\",\n" +
                "  \"cardNumber\": \"4221-1611-2233-0005\"\n" +
                "}";

        mockMvc.perform(post("/api/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketNumber").value("FL-1-ABCDEFGH"))
                .andExpect(jsonPath("$.passengerName").value("Ali Veli"))
                .andExpect(jsonPath("$.passengerEmail").value("ali@example.com"))
                .andExpect(jsonPath("$.pricePaid").value(110.00))
                .andExpect(jsonPath("$.maskedCardNumber").value("422116******0005"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        ArgumentCaptor<Long> flightIdCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> nameCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> emailCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cardCap = ArgumentCaptor.forClass(String.class);

        Mockito.verify(ticketService).purchase(
                flightIdCap.capture(),
                nameCap.capture(),
                emailCap.capture(),
                cardCap.capture()
        );
        org.junit.jupiter.api.Assertions.assertEquals(1L, flightIdCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals("Ali Veli", nameCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals("ali@example.com", emailCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals("4221-1611-2233-0005", cardCap.getValue());
    }

    @Test
    void get_shouldReturnTicketByNumber() throws Exception {
        Ticket t = sampleTicket();
        Mockito.when(ticketService.findByNumber("FL-1-ABCDEFGH")).thenReturn(t);

        mockMvc.perform(get("/api/tickets/FL-1-ABCDEFGH"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketNumber", is("FL-1-ABCDEFGH")))
                .andExpect(jsonPath("$.maskedCardNumber", is("422116******0005")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        Mockito.verify(ticketService).findByNumber("FL-1-ABCDEFGH");
    }

    @Test
    void cancel_shouldReturnCancelledTicket() throws Exception {
        Ticket cancelled = sampleTicket();
        cancelled.setStatus(TicketStatus.CANCELLED);

        Mockito.when(ticketService.cancel("FL-1-ABCDEFGH")).thenReturn(cancelled);

        mockMvc.perform(post("/api/tickets/FL-1-ABCDEFGH/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketNumber", is("FL-1-ABCDEFGH")))
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        Mockito.verify(ticketService).cancel("FL-1-ABCDEFGH");
    }
}
