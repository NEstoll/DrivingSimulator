package tests;

import application.*;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralTests {static Map<String, String> config = new HashMap<>();

    @BeforeAll
    public static void setup() {
        Scanner reader = null;
        try {
            reader = new Scanner(new File("src\\data\\testConfig.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Test Configuration file not found");
            System.err.println("Please refer to the README.txt in data");
            fail();
        }
        while (reader.hasNextLine()) {
            String next = reader.nextLine();
            if (next.startsWith("#") || next.startsWith("//")) {
                continue;
            }
            config.put(next.split("\\s+", -1)[0], next.split("\\s+", -1)[1]);
        }
    }

    @Test
    public void testVerification() throws IOException {
        if (!config.containsKey("assetto-corsa-location")) {
            assertThrows(IOException.class, DataInterface::verifyAssetto);
        } else {
            assertEquals(DataInterface.verifyAssetto(), new File(config.get("assetto-corsa-location")));
        }
    }

    @Test
    void testMain() {
        GUI.main(null);
    }
}
