package tests;

import application.FileImport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class UITests {
    static JFrame testFrame;
    @BeforeAll
    public static void setup() {
        testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Test
    public void testFileImport() {

    }

    @AfterAll
    public static void cleanup() {
        testFrame.pack();
        testFrame.setVisible(true);

    }
}
