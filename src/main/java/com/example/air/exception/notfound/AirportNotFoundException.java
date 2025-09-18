package com.example.air.exception.notfound;

public class AirportNotFoundException extends RuntimeException {
    public AirportNotFoundException(Long id) {
        super("Airport with id=" + id + " not found");
    }
}
