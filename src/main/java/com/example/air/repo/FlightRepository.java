package com.example.air.repo;

import com.example.air.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByAirline_Id(Long airlineId);
    List<Flight> findByRoute_Id(Long routeId);
    List<Flight> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
}
