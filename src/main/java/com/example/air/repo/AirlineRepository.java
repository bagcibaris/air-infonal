package com.example.air.repo;

import com.example.air.domain.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
    List<Airline> findByNameContainingIgnoreCase(String name);
}
