package com.example.air.service;

import com.example.air.domain.Airport;
import com.example.air.exception.notfound.AirportNotFoundException;
import com.example.air.repo.AirportRepository;
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
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private AirportService airportService;

    private Airport airport;

    @BeforeEach
    void setUp() {
        airport = new Airport();
        airport.setId(1L);
        airport.setName("Istanbul Airport");
        airport.setCode("IST");
    }

    @Test
    void create_shouldSaveAndReturnAirport() {
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);

        Airport result = airportService.create(airport);

        assertNotNull(result);
        assertEquals("Istanbul Airport", result.getName());
        assertEquals("IST", result.getCode());
        verify(airportRepository).save(airport);
    }

    @Test
    void searchByName_shouldReturnMatchingAirports() {
        when(airportRepository.findByNameContainingIgnoreCase("istanbul"))
                .thenReturn(Collections.singletonList(airport));

        List<Airport> result = airportService.searchByName("istanbul");

        assertEquals(1, result.size());
        assertEquals("IST", result.get(0).getCode());
        verify(airportRepository).findByNameContainingIgnoreCase("istanbul");
    }

    @Test
    void searchByName_withNullQuery_shouldDelegateEmptyString() {
        when(airportRepository.findByNameContainingIgnoreCase(""))
                .thenReturn(Arrays.asList(airport));

        List<Airport> result = airportService.searchByName(null);

        assertEquals(1, result.size());
        verify(airportRepository).findByNameContainingIgnoreCase("");
    }

    @Test
    void findByCode_shouldReturnAirports() {
        when(airportRepository.findByCodeIgnoreCase("IST"))
                .thenReturn(Collections.singletonList(airport));

        List<Airport> result = airportService.findByCode("IST");

        assertEquals(1, result.size());
        assertEquals("IST", result.get(0).getCode());
        verify(airportRepository).findByCodeIgnoreCase("IST");
    }

    @Test
    void get_shouldReturnAirport_whenFound() {
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));

        Airport result = airportService.get(1L);

        assertEquals(1L, result.getId());
        verify(airportRepository).findById(1L);
    }

    @Test
    void get_shouldThrowAirportNotFoundException_whenNotFound() {
        when(airportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AirportNotFoundException.class, () -> airportService.get(99L));
        verify(airportRepository).findById(99L);
    }
}
