package tests;

import application.DataInterface;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DataTests {
    private static DataInterface test;

    @BeforeAll
    public static void setup() {
        test = DataInterface.getInstance();
    }

    @Test
    public void testConfigs() {
        assertDoesNotThrow(() -> test.generateConfigs("abarth500"));
    }

    @Test
    public void testFileInput() {
        test.inputFile(new File(""), "Powertrain data");
        Map<String, File> expected = new HashMap<>();
        expected.put("Powertrain data", new File(""));
        assertIterableEquals(test.getInputFiles().entrySet(), expected.entrySet());
    }

    @Test
    public void testFileOutput() {

    }

    @Test
    public void testReopenLast() {

    }

}
