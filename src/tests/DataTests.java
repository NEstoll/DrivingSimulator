package tests;

import application.DataInterface;
import static org.junit.jupiter.api.Assertions.*;

import application.FileImport;
import application.GUI;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DataTests {
    private static DataInterface test;
    private File[] files = new File[] {new File("src\\data\\config.txt"), new File(""), new File("src\\data\\README.txt"), new File("src\\application\\GUI.java")};
    private String[] types = new String[]{"config", "null", "readme", "GUI class"};
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
        Map<String, File> expected = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            String s = types[i];
            test.inputFile(f, s);
            expected.put(s, f);
        }
        assertIterableEquals(test.getInputFiles().entrySet(), expected.entrySet());
    }

    @Test
    public void testFileOutput() {
        Map<String, File> expected = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            String s = types[i];
            test.inputFile(f, s);
            expected.put(s, f);
        }
        File output = new File("data\\output");
        test.generateFiles(output);

        for (String type: types) {
            System.out.println(type);
            assertTrue(new File(output,type + ".ini").exists());
        }
    }

    @Test
    public void testReopenLast() {
        //open UI, add file, close UI, reopen UI and verify change persists
        GUI.main(null);
        JFrame f = (JFrame) GUI.getFrames()[0];
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        findFileImport((JPanel) f.getContentPane()).handleFile("src\\data\\config.txt");
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
        GUI.main(null);
        f = (JFrame) GUI.getFrames()[1];
        assertEquals("src\\data\\config.txt", findFileImport((JPanel) f.getContentPane()).getLabel().getText());
    }

    public FileImport findFileImport(JPanel p) {
        for (Component c: p.getComponents()) {
            if (c instanceof FileImport) {
                return (FileImport) c;
            }
            if (c instanceof JPanel) {
                FileImport f = findFileImport((JPanel) c);
                if (f!=null) {
                    return f;
                }
            }
        }
        return null;
    }

}
