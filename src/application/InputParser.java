package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
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
            e.printStackTrace();
        }
    }

    private static void torque(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String[] next = reader.nextLine().split(",");

            try {
                if (next.length != 2) {
                    continue;
                }
                ((LUTFile)DataInterface.getOutput("power.lut")).addValue(next[0], next[1]);
            } catch (NumberFormatException e) {

            }
        }
        ((INIFile)DataInterface.getOutput("engine.ini")).setValue("[HEADER]", "POWER_CURVE", "power.lut");
    }

    private static void gears(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        int gearCount = 0;
        while (reader.hasNextLine()) {
            String[] next = reader.nextLine().split(",");
            try {
                if (next.length != 2) {
                    continue;
                }
                if (next[0].toUpperCase().matches("(R|[0-9]+|FINAL)")) {
                    switch(next[0]) {
                        case "Final":
                            next[0] = next[0].toUpperCase();
                            break;
                        case "R":
                            next[0] = "GEAR_" + next[0];
                            break;
                        default:
                            gearCount++;
                            next[0] = "GEAR_" + next[0];
                            break;
                    }
                    ((INIFile)DataInterface.getOutput("drivetrain.ini")).setValue("[GEARS]", next[0], next[1]);
                }
            } catch (NumberFormatException e) {

            }
        }
        if (gearCount == 0) {
            throw new NumberFormatException();
        }
        ((INIFile)DataInterface.getOutput("drivetrain.ini")).setValue("[GEARS]", "COUNT", String.valueOf(gearCount));
    }

    private static void suspension(File file) {

    }
}
