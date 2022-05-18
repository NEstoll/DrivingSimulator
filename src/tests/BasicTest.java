package tests;

import application.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.io.IOException;

public class BasicTest {
    @Test
    void testMain() {
        Main.main(null);
    }
    @Test
    void testAsserts() {
        assertEquals(Main.other(), 2);
    }
}
