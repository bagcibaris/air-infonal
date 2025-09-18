package com.example.air.web;

import com.example.air.domain.Airline;
import com.example.air.service.AirlineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AirlineController.class)
class AirlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirlineService airlineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturnCreatedAirline() throws Exception {
        Airline input = new Airline(1L, "Turkish Airlines", "TK");
        Mockito.when(airlineService.create(any(Airline.class))).thenReturn(input);

        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Turkish Airlines"))
                .andExpect(jsonPath("$.iataCode").value("TK"));
    }

    @Test
    void search_shouldReturnListOfAirlines() throws Exception {
        Airline a1 = new Airline(1L, "Turkish Airlines", "TK");
        Airline a2 = new Airline(2L, "Pegasus", "PC");
        Mockito.when(airlineService.search("tur")).thenReturn(Arrays.asList(a1, a2));

        mockMvc.perform(get("/api/airlines?q=tur"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Turkish Airlines")))
                .andExpect(jsonPath("$[0].iataCode", is("TK")))
                .andExpect(jsonPath("$[1].name", is("Pegasus")))
                .andExpect(jsonPath("$[1].iataCode", is("PC")));
    }

    @Test
    void get_shouldReturnAirlineById() throws Exception {
        Airline a1 = new Airline(1L, "Turkish Airlines", "TK");
        Mockito.when(airlineService.get(eq(1L))).thenReturn(a1);

        mockMvc.perform(get("/api/airlines/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Turkish Airlines"))
                .andExpect(jsonPath("$.iataCode").value("TK")); 
    }
}
