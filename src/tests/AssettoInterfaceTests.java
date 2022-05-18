package tests;

import application.Program;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AssettoInterfaceTests {
    static Map<String, String> config = new HashMap<>();

    @BeforeAll
    public static void setup() {
        Scanner reader = null;
        try {
            reader = new Scanner(new File("src\\data\\testConfig.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Test Configuration file not found");
            System.out.println("Please refer to the README.txt in data");
            System.exit(1);
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
        Program test = new Program();
        assertEquals(test.verifyAssetto(), new File(config.get("assetto-corsa-location")));
        //not sure how to test the failing condition other than running on a computer without assetto
    }

}
