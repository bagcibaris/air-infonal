package com.example.air.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirlineNotFoundExceptionTest {

    @Test
    void defaultMessage_containsId() {
        long id = 42L;
        AirlineNotFoundException ex = new AirlineNotFoundException(id);

        assertTrue(ex.getMessage().contains("Airline with id=42 not found"));
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
