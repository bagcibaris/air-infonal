package com.example.air.service;

import com.example.air.domain.Airline;
import com.example.air.exception.notfound.AirlineNotFoundException;
import com.example.air.repo.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirlineService {
    private final AirlineRepository repo;

    public Airline create(Airline a) { return repo.save(a); }

    public List<Airline> search(String name) {
        return repo.findByNameContainingIgnoreCase(name == null ? "" : name);
    }

    public Airline get(Long id) {
        return repo.findById(id).orElseThrow(() -> new AirlineNotFoundException(id));
    }
}
