package com.example.air.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class FlightFullExceptionTest {

    @Test
    void hasResponseStatusConflict() {
        ResponseStatus rs = FlightFullException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(rs, "@ResponseStatus must be present");

        assertEquals(HttpStatus.CONFLICT, rs.value(), "HTTP status should be 409 Conflict");
    }

    @Test
    void defaultMessage_containsFlightId() {
        long id = 55L;
        FlightFullException ex = new FlightFullException(id);

        assertEquals("Flight 55 is full", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
