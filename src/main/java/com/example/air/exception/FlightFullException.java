package com.example.air.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FlightFullException extends RuntimeException {
    public FlightFullException(Long flightId) {
        super("Flight " + flightId + " is full");
    }
}
