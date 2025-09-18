package com.example.air.exception.notfound;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class TicketNumberNotFoundExceptionTest {

    @Test
    void hasResponseStatusNotFound() {
        ResponseStatus rs = TicketNumberNotFoundException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(rs, "@ResponseStatus must be present");

        assertEquals(HttpStatus.NOT_FOUND, rs.value(), "HTTP status should be 404 Not Found");
    }

    @Test
    void defaultMessage_containsTicketNumber() {
        String number = "ABC12345";
        TicketNumberNotFoundException ex = new TicketNumberNotFoundException(number);

        assertEquals("Ticket with number=ABC12345 not found", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
        assertNull(ex.getCause());
    }
}
