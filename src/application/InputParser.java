package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InputParser {
    public static void parse(DataInterface.Type type, File input){
        try {
            switch (type) {
                case SUSPENSION:
                    suspension(input);
                    break;
                case TORQUE:
                    torque(input);
                    break;
                case GEARS:
                    gears(input);
                    break;

            }
        } catch (Exception e) {
            //TODO handle exception
        }
    }

    private static void torque(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String[] next = reader.nextLine().split(",");
            ((LUTFile)DataInterface.getOutput("power.lut")).addValue(next[0], next[1]);
        }
        ((INIFile)DataInterface.getOutput("engine.ini")).setValue("HEADER", "POWER_CURVE", "power.lut");
    }

    private static void gears(File file) {

    }

    private static void suspension(File file) {

    }
}
