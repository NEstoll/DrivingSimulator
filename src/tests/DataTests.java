package tests;

import application.DataInterface;
import static org.junit.jupiter.api.Assertions.*;

import application.FileImport;
import application.GUI;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class DataTests {
    private File[] files = new File[] {new File("src\\data\\example\\aero.ini"), new File("src\\data\\config.txt"), new File("src\\data\\README.txt"), new File("src\\application\\GUI.java")};
    private DataInterface.Type[] types = new DataInterface.Type[]{DataInterface.Type.AERO, DataInterface.Type.GEARS, DataInterface.Type.TORQUE, DataInterface.Type.SUSPENSION};
    @BeforeAll
    public static void setup() {
    }

    @Test
    public void testConfigs() {
        assertDoesNotThrow(() -> DataInterface.generateConfigs("abarth500"));
    }

    @Test
    public void testFileInput() {
        Map<DataInterface.Type, File> expected = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            DataInterface.Type s = types[i];
            DataInterface.inputFile(f, s);
            expected.put(s, f);
        }
        assertIterableEquals(DataInterface.getInputFiles().entrySet(), expected.entrySet());
    }

    @Test
    public void testFileOutput() {
        Map<DataInterface.Type, File> expected = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            DataInterface.Type s = types[i];
            DataInterface.inputFile(f, s);
            expected.put(s, f);
        }
        File output = new File("src\\data\\output");
        assertDoesNotThrow(() -> DataInterface.generateFiles(output));

        for (DataInterface.Type type: types) {
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
        findFileImport((JPanel) f.getContentPane()).handleFile(new File("src\\data\\config.txt"));
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
        GUI.main(null);
        f = (JFrame) GUI.getFrames()[1];
        assertEquals("config.txt", findFileImport((JPanel) f.getContentPane()).getLabel().getText());
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
