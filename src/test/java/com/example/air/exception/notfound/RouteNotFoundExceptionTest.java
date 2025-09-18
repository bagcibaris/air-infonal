package com.example.air.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteNotFoundExceptionTest {

    @Test
    void defaultMessage_containsId() {
        long id = 123L;
        RouteNotFoundException ex = new RouteNotFoundException(id);

        assertEquals("Route with id=123 not found", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
