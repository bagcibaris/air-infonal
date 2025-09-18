package com.example.air.web;

import com.example.air.domain.Airport;
import com.example.air.domain.Route;
import com.example.air.service.RouteService;
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
@WebMvcTest(controllers = RouteController.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Route sampleRoute() {
        Route r = new Route();
        r.setId(100L);
        r.setOrigin(new Airport(1L, "Istanbul Airport", "IST", "Istanbul", "TURKEY"));
        r.setDestination(new Airport(2L, "Izmir Airport", "ADB","Izmır","TURKEY"));
        return r;
    }

    @Test
    void create_shouldReturnCreatedRoute() throws Exception {
        RouteController.CreateRouteRequest req = new RouteController.CreateRouteRequest();
        req.setOriginId(1L);
        req.setDestinationId(2L);

        Route returned = sampleRoute();
        Mockito.when(routeService.create(1L, 2L)).thenReturn(returned);

        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.origin.code").value("IST"))
                .andExpect(jsonPath("$.destination.code").value("ADB"));
    }

    @Test
    void search_withOriginAndDest_shouldCallServiceWithBothCodes() throws Exception {
        Route r = sampleRoute();
        Mockito.when(routeService.search("IST", "ADB"))
                .thenReturn(Collections.singletonList(r));

        mockMvc.perform(get("/api/routes?origin=IST&dest=ADB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].origin.code", is("IST")))
                .andExpect(jsonPath("$[0].destination.code", is("ADB")));

        Mockito.verify(routeService).search("IST", "ADB");
    }

    @Test
    void search_withoutParams_shouldReturnAllRoutes() throws Exception {
        Route r1 = sampleRoute();
        Route r2 = new Route(200L, new Airport(3L, "Ankara Havalimanı", "ESB", "Ankara", "Turkey"), new Airport(4L, "Antalya Havalimanı", "AYT", "Antalya", "Turkey"));

        Mockito.when(routeService.search(null, null))
                .thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[1].id").value(200L));
    }

    @Test
    void get_shouldReturnRouteById() throws Exception {
        Route r = sampleRoute();
        Mockito.when(routeService.get(100L)).thenReturn(r);

        mockMvc.perform(get("/api/routes/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.origin.code").value("IST"))
                .andExpect(jsonPath("$.destination.code").value("ADB"));
    }
}
