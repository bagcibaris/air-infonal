package com.example.air.service;

import com.example.air.domain.Airport;
import com.example.air.domain.Route;
import com.example.air.exception.notfound.RouteNotFoundException;
import com.example.air.repo.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private AirportService airportService;

    @InjectMocks
    private RouteService routeService;

    private Airport origin;
    private Airport destination;
    private Route route;

    @BeforeEach
    void setUp() {
        origin = new Airport();
        origin.setId(1L);
        origin.setName("Istanbul Airport");
        origin.setCode("IST");

        destination = new Airport();
        destination.setId(2L);
        destination.setName("Izmir Airport");
        destination.setCode("ADB");

        route = Route.builder().id(100L).origin(origin).destination(destination).build();
    }

    @Test
    void create_shouldReturnExistingRoute_ifAlreadyExists() {
        when(airportService.get(1L)).thenReturn(origin);
        when(airportService.get(2L)).thenReturn(destination);
        when(routeRepository.findByOriginAndDestination(origin, destination))
                .thenReturn(Optional.of(route));

        Route result = routeService.create(1L, 2L);

        assertSame(route, result);
        verify(routeRepository, never()).save(any(Route.class));
        verify(airportService).get(1L);
        verify(airportService).get(2L);
    }

    @Test
    void create_shouldSaveNewRoute_ifNotExists() {
        when(airportService.get(1L)).thenReturn(origin);
        when(airportService.get(2L)).thenReturn(destination);
        when(routeRepository.findByOriginAndDestination(origin, destination))
                .thenReturn(Optional.empty());
        when(routeRepository.save(any(Route.class))).thenAnswer(inv -> {
            Route r = inv.getArgument(0);
            r.setId(200L);
            return r;
        });

        Route saved = routeService.create(1L, 2L);

        assertNotNull(saved.getId());
        assertEquals("IST", saved.getOrigin().getCode());
        assertEquals("ADB", saved.getDestination().getCode());
        verify(routeRepository).save(any(Route.class));
        verify(airportService).get(1L);
        verify(airportService).get(2L);
    }

    @Test
    void search_withBothCodes_shouldDelegateToRepo() {
        when(routeRepository.findByOrigin_CodeIgnoreCaseAndDestination_CodeIgnoreCase("IST", "ADB"))
                .thenReturn(Collections.singletonList(route));

        List<Route> result = routeService.search("IST", "ADB");

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        verify(routeRepository).findByOrigin_CodeIgnoreCaseAndDestination_CodeIgnoreCase("IST", "ADB");
    }

    @Test
    void search_withoutCodes_shouldReturnAll() {
        Route r2 = Route.builder()
                .id(101L)
                .origin(origin)
                .destination(destination)
                .build();

        when(routeRepository.findAll()).thenReturn(Arrays.asList(route, r2));

        List<Route> result = routeService.search(null, null);

        assertEquals(2, result.size());
        verify(routeRepository).findAll();
    }

    @Test
    void get_shouldReturnRoute_whenFound() {
        when(routeRepository.findById(100L)).thenReturn(Optional.of(route));

        Route found = routeService.get(100L);

        assertEquals(100L, found.getId());
        verify(routeRepository).findById(100L);
    }

    @Test
    void get_shouldThrowRouteNotFound_whenMissing() {
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.get(999L));
        verify(routeRepository).findById(999L);
    }
}
