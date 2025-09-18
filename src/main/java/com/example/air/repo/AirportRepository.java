package com.example.air.repo;

import com.example.air.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    List<Airport> findByNameContainingIgnoreCase(String name);
    List<Airport> findByCodeIgnoreCase(String code);
}
