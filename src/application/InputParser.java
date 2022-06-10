package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

class SuspensionData {

    private static SuspensionData instance = null;

    private SuspensionData() {
        referenceDistance = -1.0;
        frontHalfTrack = -1.0;
        rearHalfTrack = -1.0;
        frontLongitudinalOffset = -1.0;
        rearLongitudinalOffset = -1.0;
        frontStaticCamber = -1.0;
        rearStaticCamber = -1.0;
        frontTireDiameter = -1.0;
        rearTireDiameter = -1.0;
    }

    public static SuspensionData getInstance() {
        if (instance == null) {
            instance = new SuspensionData();
        }

        return instance;
    }

    Coordinate frontWheelCenter, rearWheelCenter;

    public void deriveWheelBase() {
        derivedWheelBase = referenceDistance + Math.abs(frontLongitudinalOffset) + Math.abs(rearLongitudinalOffset);
    }

    public class Coordinate {
        double x, y, z;
        Coordinate() {

        }
        Coordinate(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        void convertInchesToMeters() {
            double inchToMeterConst = 0.0254;
            this.x *= inchToMeterConst;
            this.y *= inchToMeterConst;
            this.z *= inchToMeterConst;
        }

        void applyAxisSwap() {
            double xc, yc, zc;
            xc = x;
            yc = y;
            zc = z;
            z = xc;
            x = -yc;
            y = zc;
        }

        String as_string() {
            return String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z);
        }
    }

    public void calculateFrontWheelCenter() {
        double adjustedHalfTrack = frontHalfTrack + frontTireDiameter / 2.0 * Math.sin(frontStaticCamber * Math.PI / 180.0);
        frontWheelCenter = new Coordinate(derivedWheelBase / 2.0, adjustedHalfTrack, frontTireDiameter / 2.0);
        frontWheelCenter.convertInchesToMeters();
    }

    public void calculateRearWheelCenter() {
        double adjustedHalfTrack = rearHalfTrack + rearTireDiameter / 2.0 * Math.sin(rearStaticCamber * Math.PI / 180.0);
        rearWheelCenter = new Coordinate(-derivedWheelBase / 2.0, adjustedHalfTrack, rearTireDiameter / 2.0);
        rearWheelCenter.convertInchesToMeters();
    }

    double referenceDistance;

    double frontHalfTrack;
    double rearHalfTrack;

    double frontLongitudinalOffset;
    double rearLongitudinalOffset;

    double frontStaticCamber;
    double rearStaticCamber;

    double frontTireDiameter;
    double rearTireDiameter;

    double derivedWheelBase;

    boolean checkFields() {
        if (-1.0 == referenceDistance) { return false; }
        if (-1.0 == frontHalfTrack) { return false; }
        if (-1.0 == rearHalfTrack) { return false; }
        if (-1.0 == frontLongitudinalOffset) { return false; }
        if (-1.0 == rearLongitudinalOffset) { return false; }
        if (-1.0 == frontStaticCamber) { return false; }
        if (-1.0 == rearStaticCamber) { return false; }
        if (-1.0 == frontTireDiameter) { return false; }
        if (-1.0 == rearTireDiameter) { return false; }
        return true;
    }
}

public class InputParser {

    public static void main(String[] args) {
        InputParser.parse(DataInterface.Type.SUSPENSION, new File("C:\\Users\\caleb\\Documents\\CSCI370\\repo\\DrivingSimulator\\src\\data\\testData\\MFX Suspension Assetto Corsa mappings - Front Suspension.csv"));
        InputParser.parse(DataInterface.Type.SUSPENSION, new File("C:\\Users\\caleb\\Documents\\CSCI370\\repo\\DrivingSimulator\\src\\data\\testData\\MFX Suspension Assetto Corsa mappings - Rear Suspension.csv"));
        InputParser.parse(DataInterface.Type.SUSPENSION, new File("C:\\Users\\caleb\\Documents\\CSCI370\\repo\\DrivingSimulator\\src\\data\\testData\\MFX Suspension Assetto Corsa mappings - Vehicle Setup.csv"));
    }

    static SuspensionData suspensionData = SuspensionData.getInstance();

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

    static String findRowEntryByKey(String key, File file) throws IOException {
        Scanner reader = new Scanner(file);

        while (reader.hasNextLine()) {
            String[] next = reader.nextLine().split(",");
            for (int i = 0; i < next.length; i++) {
                String entry = next[i];
                if (entry.equalsIgnoreCase(key)) {
                    reader.close();
                    return next[i+1];
                }
            }
        }
        reader.close();
        throw new IOException("Could not find key");
    }

    private static void suspension(File file) throws IOException {
        String value = findRowEntryByKey("Type:", file);
        if (value.equals("Vehicle Setup")) {
            suspensionData.referenceDistance = Double.parseDouble(findRowEntryByKey("Reference Distance", file));

            // Debug:
            System.out.println("Got setup suspension data");
        } else if (value.equals("Suspension")) {
            // Determine if front/rear suspension setup
            if (file.toString().toLowerCase().contains("front") && file.toString().toLowerCase().contains("rear")) {
                throw new IOException("Could not determine suspension setup filetype: front setup vs. rear setup indeterminate, please modify file names to contain only front or only rear.");
            }

            if (file.toString().toLowerCase().contains("front")) {
                // front setup
                // Get generic data:
                suspensionData.frontHalfTrack = Double.parseDouble(findRowEntryByKey("Half Track", file));
                suspensionData.frontTireDiameter = Double.parseDouble(findRowEntryByKey("Tire Diameter", file));
                suspensionData.frontLongitudinalOffset = Double.parseDouble(findRowEntryByKey("Longitudinal Offset", file));
                suspensionData.frontStaticCamber = Double.parseDouble(findRowEntryByKey("Static Camber", file));

                // Get suspension setup positions:

                // Debug:
                System.out.println("Got front suspension data");
            } else if (file.toString().toLowerCase().contains("rear")) {
                // rear setup
                // Get generic data:
                suspensionData.rearHalfTrack = Double.parseDouble(findRowEntryByKey("Half Track", file));
                suspensionData.rearTireDiameter = Double.parseDouble(findRowEntryByKey("Tire Diameter", file));
                suspensionData.rearLongitudinalOffset = Double.parseDouble(findRowEntryByKey("Longitudinal Offset", file));
                suspensionData.rearStaticCamber = Double.parseDouble(findRowEntryByKey("Static Camber", file));

                // Get suspension setup positions:

                // Debug:
                System.out.println("Got rear suspension data");
            }
        } else {
            throw new IOException("Invalid Suspension File Type: Type is not \"Vehicle Setup\" or \"Suspension\".");
        }

        if (suspensionData.checkFields()) {
            // Done reading data from all 3 files: continue
            // Derive the wheelbase:
            suspensionData.deriveWheelBase();
            // Calculate center of wheels:
            suspensionData.calculateFrontWheelCenter();
            suspensionData.calculateRearWheelCenter();

            System.out.println("Derived front wheel position: " + suspensionData.frontWheelCenter.as_string());
            System.out.println("Derived rear wheel position: " + suspensionData.rearWheelCenter.as_string());
        }
    }
}
