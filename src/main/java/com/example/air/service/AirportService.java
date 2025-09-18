package com.example.air.service;

import com.example.air.domain.Airport;
import com.example.air.exception.notfound.AirportNotFoundException;
import com.example.air.repo.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportService {
    private final AirportRepository repo;

    public Airport create(Airport a) { return repo.save(a); }

    public List<Airport> searchByName(String q) {
        return repo.findByNameContainingIgnoreCase(q == null ? "" : q);
    }

    public List<Airport> findByCode(String code) {
        return repo.findByCodeIgnoreCase(code);
    }

    public Airport get(Long id) {
        return repo.findById(id).orElseThrow(() -> new AirportNotFoundException(id));
    }
}
