package com.example.air.web;

import com.example.air.domain.Route;
import com.example.air.service.RouteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService service;

    @Data
    public static class CreateRouteRequest {
        private Long originId;
        private Long destinationId;
    }

    @PostMapping
    public Route create(@RequestBody CreateRouteRequest req) {
        return service.create(req.getOriginId(), req.getDestinationId());
    }

    @GetMapping
    public List<Route> search(@RequestParam(required = false) String origin,
                              @RequestParam(required = false) String dest) {
        return service.search(origin, dest);
    }

    @GetMapping("/{id}")
    public Route get(@PathVariable Long id) {
        return service.get(id);
    }
}
