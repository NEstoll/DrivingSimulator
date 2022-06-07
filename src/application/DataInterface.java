package application;

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
     * @return the folder containing Assetto Corsa
     * @throws IOException if Assetto Corsa can't be found
     */
    public static File getAssetto() throws IOException {
        if (assettoFolder != null) {
            return assettoFolder;
        } else {
            return findAssetto();
        }
    }

    /**
     * @param assetto The assetocorsa folder containing all of the game's data and files
     */
    public static void setAssetto(File assetto) {
        DataInterface.assettoFolder = assetto;
    }


    /**
     * @param car the name of the folder containing the configuration files to load
     * @return A mapping of files and headers to {key, value, comment} triples
     * @throws IOException if the data folder for the specified car cannot be found
     */
    public static void loadDefaultFiles(String car) throws IOException {
        File assetto = getAssetto();
        //extract data.acd
        if (!(assetto = new File(assetto, "content\\cars\\" + car)).exists()) {
            throw new FileNotFoundException("car not found at " + assetto.getAbsolutePath());
        }
        if (!(assetto = new File(assetto, "data")).exists()) {
            throw new FileNotFoundException("data folder not found at " + assetto.getAbsolutePath() + "\nmake sure you have extracted data.acd");
        }
        for (File f : assetto.listFiles()) {
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
            outputModel("test_car", outputFolder);
            //data
            File dataFolder = new File(outputFolder, "data");
            dataFolder.mkdir();
            generateDataFiles(dataFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileInterface getOutput(String f) {
        if (output.containsKey(f)) {
            return output.get(f);
        }
        FileInterface add;
        switch (f.split("\\.")[1]) {
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
        output.put(f, add);
        return add;
    }

    public static void generateDataFiles(File folder) throws IOException {
        inputFiles.forEach(InputParser::parse);
        for (Map.Entry<String, FileInterface> e: output.entrySet()) {
            File output = new File(folder, e.getKey());
            output.createNewFile();
            PrintStream out = new PrintStream(output);
            e.getValue().writeFile(out);
            out.close();
        }
    }

    public static void outputModel(String carName, File outputFolder) throws IOException {
        File car = new File(getAssetto(), "content\\cars\\" + carName);
        if (!car.exists()) {
            throw new FileNotFoundException("Unable to find reference files at " + car.getAbsolutePath());
        }
        for (File f: car.listFiles()) {
            copy(f, new File(outputFolder, f.getName()));
        }
    }

    private static void copy(File from, File to) throws IOException {
        if (from.isDirectory()) {
            if (!from.exists()) {
                from.mkdir();
            }
            for (File f: from.listFiles()) {
                copy(f, new File(to, f.getName()));
            }
        } else {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static String formatString(Type t) {
        return t.info;
    }

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

    public static void load() throws IOException {
        loadConfig();
        File prev;
        if (configs.containsKey("prev-location")) {
            prev = new File(configs.get("prev-location"));
        } else {
            prev = new File(configs.get("data-folder") + "\\prev.txt");
        }
        Scanner in;
        try {
            in = new Scanner(prev);
        } catch (FileNotFoundException e) {
            return;
        }
        while (in.hasNextLine()) {
            String next = in.nextLine();
            try {
                inputFiles.put(Type.valueOf(next.split("=")[0]), new File(next.split("=")[1]));
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
    }

    public static void save() {
        File prev;
        if (configs.containsKey("prev-location")) {
            prev = new File(configs.get("prev-location"));
        } else {
            prev = new File(configs.get("data-folder") + "\\prev.txt");
        }
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
        //set the screen name of the car (it's in ui/ui_car)
    }


    public enum Type {
        TORQUE("csv with Torque per RPM"),
        GEARS("Gear ratios, including number gears as well as reverse(R) and final"),
        AERO(""),
        SUSPENSION(""),
        NONE("");

        private String info;
        Type(String info) {
            this.info = info;
        }
    }
    public enum fileTypes {
        csv, txt, pdf, jpeg, doc
    }

    public enum fileTypeToString {
        csv {
            public String toString() {
                return "csv";
            }
        },
        txt {
            public String toString() {
                return "txt";
            }
        },
        pdf {
            public String toString() {
                return "pdf";
            }
        },
        jpeg {
            public String toString() {
                return "jpeg";
            }
        },
        doc {
            public String toString() {
                return "doc";
            }
        },
    }
}
