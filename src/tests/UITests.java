package tests;

import application.DataInterface;
import application.FileImport;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;

public class UITests {
    static JFrame testFrame;
    @BeforeAll
    public static void setup() {
        testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Test
    public void testFileImport() {
        FileImport test = new FileImport("This is a test", DataInterface.Type.NONE);
        assertEquals("No file selected", test.getLabel().getText());
        test.handleFile(new File("test.txt"));
        assertEquals("test.txt", test.getLabel().getText());

    }

    @AfterAll
    public static void cleanup() {
        testFrame.pack();
        testFrame.setVisible(true);
    }
}
