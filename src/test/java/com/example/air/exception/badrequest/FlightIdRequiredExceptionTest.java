package com.example.air.exception.badrequest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class FlightIdRequiredExceptionTest {

    @Test
    void hasResponseStatusBadRequest() {
        ResponseStatus rs = FlightIdRequiredException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(rs, "@ResponseStatus must be present");

        assertEquals(HttpStatus.BAD_REQUEST, rs.value(), "HTTP status should be 400 Bad Request");
    }

    @Test
    void defaultMessage_isCorrect() {
        FlightIdRequiredException ex = new FlightIdRequiredException();

        assertEquals("flightId is required", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
