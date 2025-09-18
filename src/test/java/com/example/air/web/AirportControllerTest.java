package com.example.air.web;

import com.example.air.domain.Airport;
import com.example.air.service.AirportService;
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
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AirportController.class)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirportService airportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturnCreatedAirport() throws Exception {
        Airport input = new Airport();
        input.setId(1L);
        input.setName("Istanbul Airport");
        input.setCode("IST");

        Mockito.when(airportService.create(any(Airport.class))).thenReturn(input);

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Istanbul Airport"))
                .andExpect(jsonPath("$.code").value("IST"));
    }

    @Test
    void search_withCodeParam_shouldCallFindByCode() throws Exception {
        Airport a1 = new Airport();
        a1.setId(1L);
        a1.setName("Istanbul Airport");
        a1.setCode("IST");

        Mockito.when(airportService.findByCode("IST"))
                .thenReturn(Collections.singletonList(a1));

        mockMvc.perform(get("/api/airports?code=IST"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Istanbul Airport")))
                .andExpect(jsonPath("$[0].code", is("IST")));

        Mockito.verify(airportService).findByCode("IST");
        Mockito.verify(airportService, Mockito.never()).searchByName(anyString());
    }

    @Test
    void search_withoutCodeParam_shouldCallSearchByName() throws Exception {
        Airport a1 = new Airport();
        a1.setId(1L);
        a1.setName("Izmir Adnan Menderes");
        a1.setCode("ADB");

        Airport a2 = new Airport();
        a2.setId(2L);
        a2.setName("Istanbul Airport");
        a2.setCode("IST");

        Mockito.when(airportService.searchByName("is"))
                .thenReturn(Arrays.asList(a1, a2));

        mockMvc.perform(get("/api/airports?q=is"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("ADB")))
                .andExpect(jsonPath("$[1].code", is("IST")));

        Mockito.verify(airportService).searchByName("is");
        Mockito.verify(airportService, Mockito.never()).findByCode(anyString());
    }

    @Test
    void get_shouldReturnAirportById() throws Exception {
        Airport a1 = new Airport();
        a1.setId(5L);
        a1.setName("Sabiha Gokcen");
        a1.setCode("SAW");

        Mockito.when(airportService.get(5L)).thenReturn(a1);

        mockMvc.perform(get("/api/airports/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Sabiha Gokcen"))
                .andExpect(jsonPath("$.code").value("SAW"));
    }
}
