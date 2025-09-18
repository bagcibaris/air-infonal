package com.example.air.service;

import com.example.air.domain.Airport;
import com.example.air.domain.Route;
import com.example.air.exception.notfound.RouteNotFoundException;
import com.example.air.repo.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository repo;
    private final AirportService airportService;

    public Route create(Long originId, Long destinationId) {
        Airport o = airportService.get(originId);
        Airport d = airportService.get(destinationId);
        return repo.findByOriginAndDestination(o, d)
                .orElseGet(() -> repo.save(Route.builder().origin(o).destination(d).build()));
    }

    public List<Route> search(String originCode, String destCode) {
        if (originCode != null && destCode != null) {
            return repo.findByOrigin_CodeIgnoreCaseAndDestination_CodeIgnoreCase(originCode, destCode);
        }
        return repo.findAll();
    }

    public Route get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RouteNotFoundException(id));
    }
}
