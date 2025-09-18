package com.example.air.service;

import com.example.air.domain.Airline;
import com.example.air.domain.Flight;
import com.example.air.domain.Route;
import com.example.air.exception.notfound.FlightNotFoundException;
import com.example.air.repo.FlightRepository;
import com.example.air.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository repo;
    private final AirlineService airlineService;
    private final RouteService routeService;

    public Flight create(Long airlineId, Long routeId, LocalDateTime dep, LocalDateTime arr,
                         int capacity, BigDecimal basePrice) {
        Airline airline = airlineService.get(airlineId);
        Route route = routeService.get(routeId);
        Flight f = Flight.builder()
                .airline(airline)
                .route(route)
                .departureTime(dep)
                .arrivalTime(arr)
                .capacity(capacity)
                .seatsSold(0)
                .basePrice(basePrice)
                .build();
        return repo.save(f);
    }

    public List<Flight> search(Long airlineId, Long routeId, LocalDate day) {
        if (day != null) {
            LocalDateTime start = day.atStartOfDay();
            LocalDateTime end = day.plusDays(1).atStartOfDay();
            return repo.findByDepartureTimeBetween(start, end);
        }
        if (airlineId != null) return repo.findByAirline_Id(airlineId);
        if (routeId != null) return repo.findByRoute_Id(routeId);
        return repo.findAll();
    }

    public Flight get(Long id) {
        return repo.findById(id).orElseThrow(() -> new FlightNotFoundException(id));
    }

    public BigDecimal currentPriceForNextSeat(Flight f) {
        return PriceCalculator.priceForNextSeat(f.getBasePrice(), f.getCapacity(), f.getSeatsSold());
    }
}
