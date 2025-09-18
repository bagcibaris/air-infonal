package com.example.air.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardMaskerTest {

    @Test
    void masksVariousFormats_toFirst6Last4() {
        assertEquals("422116******0005", CardMasker.mask("4221-1611-2233-0005"));
        assertEquals("422116******0005", CardMasker.mask("4221 1611 2233 0005"));
        assertEquals("422116******0005", CardMasker.mask("4221,1611,2233,0005"));
    }

    @Test
    void shortNumbers_returnAsIs() {
        assertEquals("1234567890", CardMasker.mask("1234-567-890"));
    }

    @Test
    void nullSafe() {
        assertNull(CardMasker.mask(null));
    }
}
