package tests;

import application.DataInterface;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DataTests {

    @Test
    public void testConfigs() {
        DataInterface test = new DataInterface();
        assertDoesNotThrow(() -> test.generateConfigs("abarth500"));
    }

}
