package com.example.air.repo;

import com.example.air.domain.Route;
import com.example.air.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByOriginAndDestination(Airport origin, Airport destination);
    List<Route> findByOrigin_CodeIgnoreCaseAndDestination_CodeIgnoreCase(String originCode, String destCode);
}
