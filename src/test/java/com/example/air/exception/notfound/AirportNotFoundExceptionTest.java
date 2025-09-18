package com.example.air.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportNotFoundExceptionTest {

    @Test
    void defaultMessage_containsId() {
        long id = 99L;
        AirportNotFoundException ex = new AirportNotFoundException(id);

        assertEquals("Airport with id=99 not found", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
