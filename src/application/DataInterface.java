package application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;

/**
 * Data class, will handle reading and writing all data to and from files, as well as any modifications that are needed.
 */
public class DataInterface {
    private static Map<Type, File> inputFiles = new HashMap<>();
    private static File assettoFolder;
    private static Map<String, String> configs = new HashMap<>();
    private static Map<String, FileInterface> output = new HashMap<>();
    private static String name;
    private static Map<Type, Action> fileListeners = new HashMap<>();
    private static File modelCar;

    public static Map<Type, File> getInputFiles() {
        return inputFiles;
    }

    /**
     * Looks for an Assetto Corsa installation on the computer, and returns the location of the install if found. Relies on the windows registry, will not work on other systems.
     *
     * @return a File pointing to the top level folder of the Assetto Corsa installation
     * @throws IOException           if registry query fails
     * @throws FileNotFoundException if Assetto Corsa isn't found
     */
    private static File findAssetto() throws IOException, FileNotFoundException { //see if Assetto is installed (only works on windows, but so does Assetto)
        //query registry for steam path
        InputStream is = Runtime.getRuntime().exec("reg query " + "\"HKEY_CURRENT_USER\\SOFTWARE\\Valve\\Steam\" /v SteamPath").getInputStream();
        StringWriter sw = new StringWriter();
        for (int c; (c = is.read()) != -1; )
            sw.write(c);
        if (sw.toString().equals("")) {
            throw new IOException();
        }
        File libraries = new File(sw.toString().substring(sw.toString().indexOf("REG_SZ    ") + "REG_SZ    ".length()).trim() + "\\steamapps\\libraryfolders.vdf");
        //read all of the libraries (places where steam stores files)
        Scanner reader = new Scanner(libraries);
        String next;
        while (reader.hasNextLine()) {
            if (!(next = reader.nextLine()).contains("path")) {
                continue;
            }
            File library = new File(next.replaceAll(".*\"(.+?)\".*", "$1") + "\\steamapps\\common\\assettocorsa");
            if (verifyAssetto(library)) {
                return library;
            }

        }
        //none found, return empty file
        return null;
    }


    /**
     * @param assetto The File representing the potential location of Assetto Corsa
     * @return true if assetto does point to the install location of Assetto Corsa; false otherwise
     */
    public static boolean verifyAssetto(File assetto) {
        return assetto != null && new File(assetto, "AssettoCorsa.exe").exists();
    }

    /**
     * @return the folder containing Assetto Corsa or null if it can't be found
     */
    public static File getAssetto() {
        if (assettoFolder != null) {
            return assettoFolder;
        } else {
            try {
                return findAssetto();
            } catch (IOException e) {
                return null;
            }
        }
    }

    /**
     * @param assetto The assetocorsa folder containing all of the game's data and files
     */
    public static void setAssetto(File assetto) {
        DataInterface.assettoFolder = assetto;
    }


    /**
     * Loads the files under the direcroty specified, and used them to both generate the .ini file mappings and copy the model/textures over as well.
     *
     * @param car the name of the folder containing the configuration files to load
     * @throws IOException if the data folder for the specified car cannot be found
     */
    public static void loadDefaultFiles(File car) throws IOException {
        if (new File(car, "data").exists() && new File(car, "data").isDirectory()) {
            modelCar = car;
            car = new File(car, "data");
        } else if (new File(getAssetto(), "content\\cars\\" + car.getName() + "\\data").exists()) {
            car = new File(getAssetto(), "content\\cars\\" + car.getName() + "\\data");
            modelCar = car.getParentFile();
        } else if (new File(getAssetto(), "content\\cars\\" + car.getName()).exists()) {
            throw new FileNotFoundException("data folder not found at " + getAssetto().getAbsolutePath() + "\nmake sure you have extracted data.acd");
        }
        if (new File(car.getParent(), "ui\\ui_car.json").exists()) {
            Scanner input = new Scanner(new File(car.getParent(), "ui\\ui_car.json"));
            while (input.hasNext()) {
                String next = input.nextLine();
                if (next.trim().startsWith("\"name\"")) {
                    setName(next.split("\"")[3]);
                    break;
                }
            }
        }
        if (new File(car.getParent(), "files.txt").exists() && new File(car.getParent(), "config.txt").exists()) {
            load(new File(car.getParent(), "files.txt"));
        }
        for (File f : car.listFiles()) {
            FileInterface fileIO;
            switch (f.getName().split("\\.")[1]) {
                case "ini":
                    fileIO = new INIFile();
                    break;
                case "lut":
                    fileIO = new LUTFile();
                    break;
                case "rto":
                    fileIO = new RTOFile();
                    break;
                default:
                    //unsupported file type
                    return;
            }
            fileIO.readFile(f);
            output.put(f.getName(), fileIO);
        }
    }

    /**
     * @param file The input file
     * @param type The type of file.
     */
    public static void inputFile(File file, Type type) {
        if (file == null) {
            inputFiles.remove(type);
        } else {
            inputFiles.put(type, file);
        }
    }

    public static void outputFiles(File outputFolder) {
        try {
            outputFolder.mkdir();
            //model
            outputModel(modelCar != null ? modelCar : new File(getAssetto(), "content\\cars\\test_car"), outputFolder);
            //name
            outputName(name);
            //data
            File dataFolder = new File(outputFolder, "data");
            dataFolder.mkdir();
            generateDataFiles(dataFolder);
            //configs
            Files.copy(new File(configs.get("data-folder") + "\\config.txt").toPath(), new File(outputFolder, "config.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
            save(new File(outputFolder, "files.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets or creates a FileInterface mapped to by the provided string and returns it
     *
     * @param fileName
     * @return FileInterface of the correct type (ini, lut, rto, etc)
     */
    public static FileInterface getOutput(String fileName) {
        if (output.containsKey(fileName)) {
            return output.get(fileName);
        }
        FileInterface add;
        switch (fileName.split("\\.")[1]) {
            case "ini":
                add = new INIFile();
                break;
            case "lut":
                add = new LUTFile();
                break;
            case "rto":
                add = new RTOFile();
                break;
            default:
                //unsupported file type
                return null;
        }
        output.put(fileName, add);
        return add;
    }

    /**
     * @param folder The folder to which the data files will be output
     * @throws IOException if folder doesn't exist and is unable to be created
     */
    public static void generateDataFiles(File folder) throws IOException {
        inputFiles.forEach(InputParser::parse);
        for (Map.Entry<String, FileInterface> e : output.entrySet()) {
            File output = new File(folder, e.getKey());
            output.createNewFile();
            PrintStream out = new PrintStream(output);
            e.getValue().writeFile(out);
            out.close();
        }
    }

    /**
     * Copies all non data files from the car folder to the output folder
     *
     * @param car          Folder containing files to be copied
     * @param outputFolder Location to copy files to
     * @throws IOException
     */
    public static void outputModel(File car, File outputFolder) throws IOException {
        if (!car.exists()) {
            throw new FileNotFoundException("Unable to find reference files at " + car.getAbsolutePath());
        }
        for (File f : car.listFiles()) {
            copy(f, new File(outputFolder, f.getName()));
        }
    }

    private static void copy(File from, File to) throws IOException {
        if (from.isDirectory()) {
            if (!to.exists()) {
                to.mkdir();
            }
            for (File f : from.listFiles()) {
                copy(f, new File(to, f.getName()));
            }
        } else {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns info string associated with the type passed
     * @param t
     * @return
     */
    public static String formatString(Type t) {
        return t.info;
    }

    /**
     * Loads configuration files from the default location
     *
     * @throws IOException If config file can't be found, and can't be created.
     */
    public static void loadConfig() throws IOException {
        File config;
        if ((config = new File("src\\data\\config.txt")).exists()) {
            //dev location
            configs.put("data-folder", "src\\data");
        } else if ((config = new File("data\\config.txt")).exists()) {
            //eventual location of data folder
            configs.put("data-folder", "data");
        } else {
            config.getParentFile().mkdir();
            config.createNewFile();
            //default configs
            configs.put("data-folder", "data");
        }
        loadConfig(config);
    }

    private static void loadConfig(File config) throws FileNotFoundException {
        configs.put("data-folder", config.getParent());
        Scanner in = new Scanner(config);
        while (in.hasNextLine()) {
            String next = in.nextLine();
            if (next.startsWith("#") || next.startsWith("//")) {
                continue;
            }
            try {
                configs.put(next.split("=")[0], next.split("=")[1]);
            } catch (Exception e) {
                //bad config, ignore
            }
        }
    }

    /**
     * Loads previous files
     *
     * @throws IOException if configs cannot be loaded
     */
    public static void load() throws IOException {
        loadConfig();
        File prev;
        if (configs.containsKey("prev-location")) {
            prev = new File(configs.get("prev-location"));
        } else {
            prev = new File(configs.get("data-folder") + "\\prev.txt");
        }
        load(prev);
    }

    public static void addfileListener(Type t, Action a) {
        fileListeners.put(t, a);
    }

    private static void load(File prev) throws FileNotFoundException {
        loadConfig(new File(prev.getParentFile(), "config.txt"));
        Scanner in;
        try {
            in = new Scanner(prev);
        } catch (FileNotFoundException e) {
            return;
        }
        while (in.hasNextLine()) {
            String next = in.nextLine();
            try {
                Type t = Type.valueOf(next.split("=")[0]);
                File f = new File(next.split("=")[1]);
                inputFiles.put(t, f);
                if (fileListeners.containsKey(t)) {
                    fileListeners.get(t).actionPerformed(null);
                }
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
    }

    /**
     * Saves currently selected files to disk
     */
    public static void save() {
        File prev;
        if (configs.containsKey("prev-location")) {
            prev = new File(configs.get("prev-location"));
        } else {
            prev = new File(configs.get("data-folder") + "\\prev.txt");
        }
        save(prev);
    }

    private static void save(File prev) {
        try {
            prev.createNewFile();
            PrintStream out = new PrintStream(prev);
            for (Map.Entry e : inputFiles.entrySet()) {
                out.println(e.getKey() + "=" + e.getValue());
            }
        } catch (IOException e) {
            System.err.println("Unable to save");
            e.printStackTrace();
        }
    }

    public static Map<String, String> getConfigs() {
        return configs;
    }

    public static void setName(String text) {
        name = text;
        GUI.setNameText(DataInterface.getName());
    }

    public static void outputName(String text) {
        //in ui/ui_car
    }

    public static String getName() {
        return name;
    }


    public enum Type {
    	// Power train
        TORQUE("csv with Torque per RPM"),
        GEARS("Gear ratios, including number gears as well as reverse(R) and final"),
        // Aero - Not done on this project but info message would be added in quotations, can add other enum types that are relevant to Aero
        AERO(""),
        // Suspension
        SUSPENSION(""),
        NONE("");

        private String info;

        Type(String info) {
            this.info = info;
        }
    }
}
