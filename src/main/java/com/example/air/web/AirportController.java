package com.example.air.web;

import com.example.air.domain.Airport;
import com.example.air.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService service;

    @PostMapping
    public Airport create(@RequestBody Airport body) {
        return service.create(body);
    }

    @GetMapping
    public List<Airport> search(@RequestParam(required = false) String q,
                                @RequestParam(required = false) String code) {
        return (code != null && !code.isEmpty())
                ? service.findByCode(code)
                : service.searchByName(q);
    }

    @GetMapping("/{id}")
    public Airport get(@PathVariable Long id) {
        return service.get(id);
    }
}
