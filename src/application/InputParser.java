package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Geometry {
    public static Map<String,String> csvToIniStrings = new HashMap<>();
    Map<String, Coordinate> data = new HashMap<>();


    Geometry() {
        csvToIniStrings.put("CHAS_LowFor","WBCAR_BOTTOM_FRONT");
        csvToIniStrings.put("CHAS_LowAft","WBCAR_BOTTOM_REAR");
        csvToIniStrings.put("CHAS_UppFor","WBCAR_TOP_FRONT");
        csvToIniStrings.put("CHAS_UppAft","WBCAR_TOP_REAR");
        csvToIniStrings.put("UPRI_UppPnt","WBTYRE_TOP");
        csvToIniStrings.put("UPRI_LowPnt","WBTYRE_BOTTOM");
        csvToIniStrings.put("CHAS_TiePnt","WBCAR_STEER");
        csvToIniStrings.put("UPRI_TiePnt","WBTYRE_STEER");
        for (String key : csvToIniStrings.keySet()) {
            data.put(key, new Coordinate());
        }
    }
}

class Coordinate {
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

    static double asMeters(double inches) {
        return inches *= 0.0254;
    }

    void recenter(Coordinate newZero) {
        x = x - newZero.x;
        y = y - newZero.y;
        z = z - newZero.z;
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
        return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
    }
}
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

    public static double toeDegreesToMeters(double toeDeg, double tireDiameterInches) {
        return Math.sin(toeDeg * Math.PI/180.0) * tireDiameterInches * 0.0254;
    }

    public static double findBasey(double radiusIn, double cgHeightIn) {
        return 0.0254 * (cgHeightIn - radiusIn);
    }

    double referenceDistance;

    double cgHeightIn;
    double cgFrontRearDistribution;

    double frontHalfTrack;
    double rearHalfTrack;

    double frontLongitudinalOffset;
    double rearLongitudinalOffset;

    double frontStaticCamber;
    double rearStaticCamber;

    double frontStaticToeDeg;
    double rearStaticToeDeg;

    double frontTireDiameter;
    double rearTireDiameter;

    double derivedWheelBase;

    Geometry frontGeometry = new Geometry();
    Geometry rearGeometry = new Geometry();

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
        InputParser.parse(DataInterface.Type.VEHICLESETUP, new File("src\\data\\testData\\MFX Suspension Assetto Corsa mappings - Front Suspension.csv"));
        InputParser.parse(DataInterface.Type.VEHICLESETUP, new File("src\\data\\testData\\MFX Suspension Assetto Corsa mappings - Rear Suspension.csv"));
        InputParser.parse(DataInterface.Type.VEHICLESETUP, new File("src\\data\\testData\\MFX Suspension Assetto Corsa mappings - Vehicle Setup.csv"));
    }

    static SuspensionData suspensionData = SuspensionData.getInstance();

    public static void parse(DataInterface.Type type, File input){
        try {
            switch (type) {
                case VEHICLESETUP:
                case FRONT:
                case REAR:
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
        ((LUTFile)DataInterface.getOutput("power.lut")).clear();
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

    static Coordinate getCoordinateByKey(String key, File file) throws IOException {
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String[] next = reader.nextLine().split(",");
            for (int i = 0; i < next.length; i++) {
                String entry = next[i];
                if (entry.toLowerCase().contains(key.toLowerCase())) {
                    reader.close();
                    return new Coordinate(Double.parseDouble(next[i+1]), Double.parseDouble(next[i+2]), Double.parseDouble(next[i+3]));
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

            suspensionData.cgFrontRearDistribution = Double.parseDouble(findRowEntryByKey("Front Mass Distribution", file)) / 100.0;

            suspensionData.cgHeightIn = Double.parseDouble(findRowEntryByKey("Center of Gravity Height", file));

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
                suspensionData.frontStaticToeDeg = Double.parseDouble(findRowEntryByKey("Static Toe", file));

                // Get suspension setup positions:
                for (String key : Geometry.csvToIniStrings.keySet()) {
                    Coordinate geometryPosition = getCoordinateByKey(key, file);
                    geometryPosition.convertInchesToMeters();
                    suspensionData.frontGeometry.data.put(key, geometryPosition);
                }


                // Debug:
                System.out.println("Got front suspension data");
            } else if (file.toString().toLowerCase().contains("rear")) {
                // rear setup
                // Get generic data:
                suspensionData.rearHalfTrack = Double.parseDouble(findRowEntryByKey("Half Track", file));
                suspensionData.rearTireDiameter = Double.parseDouble(findRowEntryByKey("Tire Diameter", file));
                suspensionData.rearLongitudinalOffset = Double.parseDouble(findRowEntryByKey("Longitudinal Offset", file));
                suspensionData.rearStaticCamber = Double.parseDouble(findRowEntryByKey("Static Camber", file));
                suspensionData.rearStaticToeDeg = Double.parseDouble(findRowEntryByKey("Static Toe", file));

                // Get suspension setup positions:
                for (String key : Geometry.csvToIniStrings.keySet()) {
                    Coordinate geometryPosition = getCoordinateByKey(key, file);
                    geometryPosition.convertInchesToMeters();
                    suspensionData.rearGeometry.data.put(key, geometryPosition);
                }

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

            // Recenter geometry data
            for (String key : suspensionData.frontGeometry.data.keySet()) {
                suspensionData.frontGeometry.data.get(key).recenter(suspensionData.frontWheelCenter);
            }
            for (String key : suspensionData.rearGeometry.data.keySet()) {
                suspensionData.rearGeometry.data.get(key).recenter(suspensionData.rearWheelCenter);
            }

            // Reorient geometry data
            for (String key : suspensionData.frontGeometry.data.keySet()) {
                suspensionData.frontGeometry.data.get(key).applyAxisSwap();
                // Output to ini
                ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[FRONT]", Geometry.csvToIniStrings.get(key).toUpperCase(), suspensionData.frontGeometry.data.get(key).as_string());
            }
            for (String key : suspensionData.rearGeometry.data.keySet()) {
                suspensionData.rearGeometry.data.get(key).applyAxisSwap();
                // Output to ini
                ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[REAR]", Geometry.csvToIniStrings.get(key).toUpperCase(), suspensionData.rearGeometry.data.get(key).as_string());
            }

            // Additional ini outputs:
            // [HEADER]
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[HEADER]", "VERSION", "4");

            // [_EXTENSION]
            // TORQUE_MODE_EX
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[_EXTENSION]", "TORQUE_MODE_EX", "2");
            // FIX_PROGRESSIVE_RATE
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[_EXTENSION]", "FIX_PROGRESSIVE_RATE", "1");
            // USE_DWB2
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[_EXTENSION]", "USE_DWB2", "1");

            // [BASIC]
            // WHEELBASE
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[BASIC]", "WHEELBASE", String.valueOf(Coordinate.asMeters(suspensionData.derivedWheelBase)));
            // CG_LOCATION
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[BASIC]", "CG_LOCATION", String.valueOf(suspensionData.cgFrontRearDistribution));

            // [FRONT]
            // TYPE
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[FRONT]", "TYPE", "DWB");
            // TRACK
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[FRONT]", "TRACK", String.valueOf(suspensionData.frontWheelCenter.y * 2));
            // BASEY
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[FRONT]", "BASEY", String.valueOf(SuspensionData.findBasey(suspensionData.frontTireDiameter / 2.0, suspensionData.cgHeightIn)));
            // STATIC_CAMBER
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[FRONT]", "STATIC_CAMBER", String.valueOf(suspensionData.frontStaticCamber));
            // TOE_OUT
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[FRONT]", "TOE_OUT", String.valueOf(SuspensionData.toeDegreesToMeters(suspensionData.frontStaticToeDeg, suspensionData.frontTireDiameter)));

            // [REAR]
            // TYPE
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[REAR]", "TYPE", "DWB");
            // TRACK
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[REAR]", "TRACK", String.valueOf(suspensionData.rearWheelCenter.y * 2));
            // BASEY
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[REAR]", "BASEY", String.valueOf(SuspensionData.findBasey(suspensionData.rearTireDiameter / 2.0, suspensionData.cgHeightIn)));
            // STATIC_CAMBER
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[REAR]", "STATIC_CAMBER", String.valueOf(suspensionData.rearStaticCamber));
            // TOE_OUT
            ((INIFile)DataInterface.getOutput("suspensions.ini")).setValue("[REAR]", "TOE_OUT", String.valueOf(SuspensionData.toeDegreesToMeters(suspensionData.rearStaticToeDeg, suspensionData.rearTireDiameter)));

            // Debug:
            System.out.println("Derived front wheel position: " + suspensionData.frontWheelCenter.as_string());
            System.out.println("Derived rear wheel position: " + suspensionData.rearWheelCenter.as_string());
            System.out.println("Front basey (meters): " + SuspensionData.findBasey(suspensionData.frontTireDiameter / 2.0, suspensionData.cgHeightIn));
            System.out.println("Rear basey (meters): " + SuspensionData.findBasey(suspensionData.rearTireDiameter / 2.0, suspensionData.cgHeightIn));
            System.out.println("Front toe (meters): " + SuspensionData.toeDegreesToMeters(suspensionData.frontStaticToeDeg, suspensionData.frontTireDiameter));
            System.out.println("Rear toe (meters): " + SuspensionData.toeDegreesToMeters(suspensionData.rearStaticToeDeg, suspensionData.rearTireDiameter));
            System.out.println("Front Geometry:");
            for (String key : suspensionData.frontGeometry.data.keySet()) {
                System.out.println(key + "/ " + Geometry.csvToIniStrings.get(key) + ": " + suspensionData.frontGeometry.data.get(key).as_string());
            }
            System.out.println("Rear Geometry:");
            for (String key : suspensionData.rearGeometry.data.keySet()) {
                System.out.println(key + "/ " + Geometry.csvToIniStrings.get(key) + ": " + suspensionData.rearGeometry.data.get(key).as_string());
            }
        }
    }
}
