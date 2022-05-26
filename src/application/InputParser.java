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
                case POWER:
                    power(input);
                    break;
                case GEARS:
                    gears(input);
                    break;

            }
        } catch (Exception e) {
            //TODO handle exception
        }
    }

    private static void power(File file) throws FileNotFoundException {
        //TODO actual code
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String[] next = reader.nextLine().split(",");
            DataInterface.getOutput(new File("power.lut")).setValue("", next[0], next[1]);
        }
    }

    private static void gears(File file) {

    }

    private static void suspension(File file) {

    }
}
