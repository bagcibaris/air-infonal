package com.example.air.service;

import com.example.air.domain.Airline;
import com.example.air.domain.Flight;
import com.example.air.domain.Route;
import com.example.air.exception.notfound.FlightNotFoundException;
import com.example.air.repo.FlightRepository;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirlineService airlineService;

    @Mock
    private RouteService routeService;

    @InjectMocks
    private FlightService flightService;

    private Airline airline;
    private Route route;

    @BeforeEach
    void setUp() {
        airline = new Airline(11L, "Test Airline", "TA");
        route = new Route();
        route.setId(22L);
    }

    @Test
    void create_shouldAssembleEntityAndSave() {
        LocalDateTime dep = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime arr = LocalDateTime.of(2025, 2, 1, 12, 0);
        int capacity = 150;
        BigDecimal base = new BigDecimal("99.90");

        when(airlineService.get(11L)).thenReturn(airline);
        when(routeService.get(22L)).thenReturn(route);
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> {
            Flight f = inv.getArgument(0);
            f.setId(99L);
            return f;
        });

        Flight saved = flightService.create(11L, 22L, dep, arr, capacity, base);

        assertNotNull(saved.getId());
        assertEquals(airline, saved.getAirline());
        assertEquals(route, saved.getRoute());
        assertEquals(dep, saved.getDepartureTime());
        assertEquals(arr, saved.getArrivalTime());
        assertEquals(capacity, saved.getCapacity());
        assertEquals(0, saved.getSeatsSold());
        assertEquals(base, saved.getBasePrice());

        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository).save(captor.capture());
        assertEquals(0, captor.getValue().getSeatsSold());

        verify(airlineService).get(11L);
        verify(routeService).get(22L);
    }

    @Test
    void search_byDay_shouldQueryBetweenStartAndEnd() {
        LocalDate day = LocalDate.of(2025, 2, 1);
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay();

        when(flightRepository.findByDepartureTimeBetween(start, end))
                .thenReturn(Collections.emptyList());

        var result = flightService.search(null, null, day);

        assertNotNull(result);
        verify(flightRepository).findByDepartureTimeBetween(start, end);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void search_byAirline_shouldCallFindByAirlineId() {
        when(flightRepository.findByAirline_Id(11L)).thenReturn(Arrays.asList(new Flight()));

        var result = flightService.search(11L, null, null);

        assertEquals(1, result.size());
        verify(flightRepository).findByAirline_Id(11L);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void search_byRoute_shouldCallFindByRouteId() {
        when(flightRepository.findByRoute_Id(22L)).thenReturn(Arrays.asList(new Flight()));

        var result = flightService.search(null, 22L, null);

        assertEquals(1, result.size());
        verify(flightRepository).findByRoute_Id(22L);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void search_all_shouldReturnFindAllWhenNoFilters() {
        when(flightRepository.findAll()).thenReturn(Arrays.asList(new Flight(), new Flight()));

        var result = flightService.search(null, null, null);

        assertEquals(2, result.size());
        verify(flightRepository).findAll();
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void get_shouldReturnFlight_whenFound() {
        Flight f = new Flight();
        f.setId(77L);
        when(flightRepository.findById(77L)).thenReturn(Optional.of(f));

        Flight result = flightService.get(77L);

        assertEquals(77L, result.getId());
        verify(flightRepository).findById(77L);
    }

    @Test
    void get_shouldThrowFlightNotFound_whenMissing() {
        when(flightRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> flightService.get(123L));
        verify(flightRepository).findById(123L);
    }

    @Test
    void currentPriceForNextSeat_thresholdsAreApplied() {
        Flight f1 = Flight.builder().basePrice(new BigDecimal("100")).capacity(100).seatsSold(9).build();
        Flight f2 = Flight.builder().basePrice(new BigDecimal("100")).capacity(100).seatsSold(10).build();

        assertEquals(0, flightService.currentPriceForNextSeat(f1).compareTo(new BigDecimal("100")));
        assertEquals(0, flightService.currentPriceForNextSeat(f2).compareTo(new BigDecimal("110")));
    }
}
