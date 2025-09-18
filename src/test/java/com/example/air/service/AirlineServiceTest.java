package com.example.air.service;

import com.example.air.domain.Airline;
import com.example.air.exception.notfound.AirlineNotFoundException;
import com.example.air.repo.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    private Airline airline;

    @BeforeEach
    void setUp() {
        airline = new Airline(1L, "Turkish Airlines", "TK");
    }

    @Test
    void create_shouldSaveAndReturnAirline() {
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline);

        Airline result = airlineService.create(new Airline(null, "Turkish Airlines", "TK"));

        assertNotNull(result);
        assertEquals("Turkish Airlines", result.getName());
        assertEquals("TK", result.getIataCode());
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    void search_shouldReturnMatchingAirlines() {
        when(airlineRepository.findByNameContainingIgnoreCase("tur"))
                .thenReturn(Arrays.asList(airline));

        List<Airline> result = airlineService.search("tur");

        assertEquals(1, result.size());
        assertEquals("Turkish Airlines", result.get(0).getName());
        verify(airlineRepository).findByNameContainingIgnoreCase("tur");
    }

    @Test
    void search_withNullName_shouldDelegateWithEmptyString() {
        when(airlineRepository.findByNameContainingIgnoreCase(""))
                .thenReturn(Arrays.asList(airline));

        List<Airline> result = airlineService.search(null);

        assertEquals(1, result.size());
        verify(airlineRepository).findByNameContainingIgnoreCase("");
    }

    @Test
    void get_shouldReturnAirline_whenFound() {
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        Airline result = airlineService.get(1L);

        assertEquals(1L, result.getId());
        verify(airlineRepository).findById(1L);
    }

    @Test
    void get_shouldThrowAirlineNotFoundException_whenNotFound() {
        when(airlineRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AirlineNotFoundException.class, () -> airlineService.get(2L));
        verify(airlineRepository).findById(2L);
    }
}
