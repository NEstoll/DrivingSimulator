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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DataTests {
    private File[] files = new File[] {new File("src\\data\\example\\aero.ini"), new File("src\\data\\config.txt"), new File("src\\data\\README.txt"), new File("src\\application\\GUI.java")};
    private DataInterface.Type[] types = new DataInterface.Type[]{DataInterface.Type.AERO, DataInterface.Type.GEARS, DataInterface.Type.TORQUE, DataInterface.Type.SUSPENSION};
    @BeforeAll
    public static void setup() {
    }

    @Test
    public void testConfigs() {
        assertDoesNotThrow(() -> DataInterface.loadDefaultFiles("abarth500"));
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
    public void testPowerTrain() {
        File inputFile;
        DataInterface.inputFile((inputFile = new File("src\\data\\testData\\torqueCurve.txt")), DataInterface.Type.TORQUE);
        assertTrue(inputFile.exists());
        assertDoesNotThrow(() -> DataInterface.generateDataFiles(new File("src\\data\\output")));
        File f;
        assertTrue((f =new File("src\\data\\output\\power.lut")).exists());
        final Scanner[] input = new Scanner[1];
        assertDoesNotThrow(() -> {input[0] = new Scanner(inputFile);});
        final Scanner[] out = new Scanner[1];
        assertDoesNotThrow(() -> {out[0] = new Scanner(f);});
        while (input[0].hasNextLine()) {
            assertTrue(out[0].hasNextLine());
            String next = input[0].nextLine();
            if (!next.matches("(([0-9](.[0-9]+)?)+,)*([0-9](.[0-9]+)?)+")) {
                continue;
            }
            String next2 = out[0].nextLine();
            Arrays.stream(next.split(",")).forEach((s) -> assertTrue(next2.contains(s)));
        }
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
        assertDoesNotThrow(() -> DataInterface.generateDataFiles(output));

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
