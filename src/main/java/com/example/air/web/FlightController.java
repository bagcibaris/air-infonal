package com.example.air.web;

import com.example.air.domain.Flight;
import com.example.air.service.FlightService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService service;

    @Data
    public static class CreateFlightRequest {
        private Long airlineId;
        private Long routeId;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private int capacity;
        private BigDecimal basePrice;
    }

    @PostMapping
    public Flight create(@RequestBody CreateFlightRequest req) {
        return service.create(
                req.getAirlineId(),
                req.getRouteId(),
                req.getDepartureTime(),
                req.getArrivalTime(),
                req.getCapacity(),
                req.getBasePrice()
        );
    }

    @GetMapping
    public List<Flight> search(@RequestParam(required = false) Long airlineId,
                               @RequestParam(required = false) Long routeId,
                               @RequestParam(required = false) String departureDate) {
        LocalDate day = (departureDate != null && !departureDate.isEmpty())
                ? LocalDate.parse(departureDate)
                : null;
        return service.search(airlineId, routeId, day);
    }

    @GetMapping("/{id}/price")
    public BigDecimal priceForNextSeat(@PathVariable Long id) {
        return service.currentPriceForNextSeat(service.get(id));
    }
}
