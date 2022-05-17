package tests;

import application.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class BasicTest {
    @Test
    void testMain() {
        Main.main(null);
    }

    void testAsserts() {
        assertEquals(Main.other(), 2);
    }
}
