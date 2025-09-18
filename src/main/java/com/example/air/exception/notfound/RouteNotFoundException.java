package com.example.air.exception.notfound;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(Long id) {
        super("Route with id=" + id + " not found");
    }
}
