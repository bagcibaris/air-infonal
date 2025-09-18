package com.example.air.exception.notfound;

public class AirlineNotFoundException extends RuntimeException {
    public AirlineNotFoundException(Long id) {
        super("Airline with id=" + id + " not found");
    }
}
