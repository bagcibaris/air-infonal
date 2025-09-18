package com.example.air.web;

import com.example.air.domain.Airline;
import com.example.air.domain.Airport;
import com.example.air.domain.Flight;
import com.example.air.domain.Route;
import com.example.air.service.FlightService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private Flight sampleFlight() {
        Flight f = new Flight();
        f.setId(99L);
        f.setAirline(new Airline(11L, "Test Airline", "TA"));
        f.setRoute(new Route(22L, new Airport(), new Airport()));
        f.setDepartureTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        f.setArrivalTime(LocalDateTime.of(2025, 2, 1, 12, 0));
        f.setCapacity(150);
        f.setSeatsSold(10);
        f.setBasePrice(new BigDecimal("100.00"));
        return f;
    }

    @Test
    void create_shouldCallServiceAndReturnFlight() throws Exception {
        FlightController.CreateFlightRequest req = new FlightController.CreateFlightRequest();
        req.setAirlineId(11L);
        req.setRouteId(22L);
        req.setDepartureTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        req.setArrivalTime(LocalDateTime.of(2025, 2, 1, 12, 0));
        req.setCapacity(150);
        req.setBasePrice(new BigDecimal("100.00"));

        Flight returned = sampleFlight();

        Mockito.when(flightService.create(
                eq(11L), eq(22L),
                any(LocalDateTime.class), any(LocalDateTime.class),
                eq(150), eq(new BigDecimal("100.00"))
        )).thenReturn(returned);

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.capacity").value(150))
                .andExpect(jsonPath("$.seatsSold").value(10))
                .andExpect(jsonPath("$.basePrice").value(100.00));

        ArgumentCaptor<Long> airlineIdCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> routeIdCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDateTime> depCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> arrCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Integer> capCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<BigDecimal> baseCap = ArgumentCaptor.forClass(BigDecimal.class);

        Mockito.verify(flightService).create(
                airlineIdCap.capture(),
                routeIdCap.capture(),
                depCap.capture(),
                arrCap.capture(),
                capCap.capture(),
                baseCap.capture()
        );
        org.junit.jupiter.api.Assertions.assertEquals(11L, airlineIdCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(22L, routeIdCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(LocalDateTime.of(2025, 2, 1, 10, 0), depCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(LocalDateTime.of(2025, 2, 1, 12, 0), arrCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(150, capCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(0, baseCap.getValue().compareTo(new BigDecimal("100.00")));
    }

    @Test
    void search_withDepartureDate_shouldParseAndCallService() throws Exception {
        Flight f = sampleFlight();
        Mockito.when(flightService.search(isNull(), isNull(), eq(LocalDate.of(2025, 2, 1))))
                .thenReturn(Collections.singletonList(f));

        mockMvc.perform(get("/api/flights?departureDate=2025-02-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(99L));

        Mockito.verify(flightService).search(null, null, LocalDate.of(2025, 2, 1));
    }

    @Test
    void search_withAirlineId_shouldCallServiceWithAirlineFilter() throws Exception {
        Flight f = sampleFlight();
        Mockito.when(flightService.search(eq(11L), isNull(), isNull()))
                .thenReturn(Collections.singletonList(f));

        mockMvc.perform(get("/api/flights?airlineId=11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Mockito.verify(flightService).search(11L, null, null);
    }

    @Test
    void search_withRouteId_shouldCallServiceWithRouteFilter() throws Exception {
        Flight f = sampleFlight();
        Mockito.when(flightService.search(isNull(), eq(22L), isNull()))
                .thenReturn(Collections.singletonList(f));

        mockMvc.perform(get("/api/flights?routeId=22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Mockito.verify(flightService).search(null, 22L, null);
    }

    @Test
    void priceForNextSeat_shouldReturnBigDecimalNumber() throws Exception {
        Flight f = sampleFlight();
        Mockito.when(flightService.get(99L)).thenReturn(f);
        Mockito.when(flightService.currentPriceForNextSeat(f)).thenReturn(new BigDecimal("110.00"));

        mockMvc.perform(get("/api/flights/99/price"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(110.00)));

        Mockito.verify(flightService).get(99L);
        Mockito.verify(flightService).currentPriceForNextSeat(f);
    }
}
