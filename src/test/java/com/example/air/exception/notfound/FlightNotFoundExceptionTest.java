package com.example.air.exception.notfound;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class FlightNotFoundExceptionTest {

    @Test
    void hasResponseStatusNotFound() {
        ResponseStatus rs = FlightNotFoundException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(rs, "@ResponseStatus must be present");

        assertEquals(HttpStatus.NOT_FOUND, rs.value(), "HTTP status should be 404 Not Found");
    }

    @Test
    void defaultMessage_containsId() {
        long id = 77L;
        FlightNotFoundException ex = new FlightNotFoundException(id);

        assertEquals("Flight with id=77 not found", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
