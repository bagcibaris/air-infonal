package com.example.air.web;

import com.example.air.domain.Airline;
import com.example.air.service.AirlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/airlines")
public class AirlineController {

    private final AirlineService service;

    @PostMapping
    public Airline create(@RequestBody Airline body) {
        return service.create(body);
    }

    @GetMapping
    public List<Airline> search(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/{id}")
    public Airline get(@PathVariable Long id) {
        return service.get(id);
    }
}
