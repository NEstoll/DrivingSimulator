package application;

import java.io.*;
import java.util.*;

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
    public static Map<File, Map<String, ArrayList<String[]>>> generateConfigs(String car) throws IOException {
        File assetto = getAssetto();
        //extract data.acd
        if (!(assetto = new File(assetto, "content\\cars\\" + car + "\\data")).exists()) {
            throw new FileNotFoundException("data folder not found at " + assetto.getAbsolutePath());
        }
        Map<File, Map<String, ArrayList<String[]>>> configuration = new HashMap<>();
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
                    return null;
            }
            fileIO.readFile(f);
            output.put(f.getName(), fileIO);

            //just here for advanced mode
            Scanner fileReader = new Scanner(f);
            Map<String, ArrayList<String[]>> headers = new HashMap<>();
            ArrayList<String[]> config = new ArrayList<>();
            while (fileReader.hasNextLine()) {
                String next = fileReader.nextLine();
                if (next.startsWith("[") && next.endsWith("]")) {
                    headers.put(next, (config = new ArrayList<>()));
                    continue;
                } else if (!next.contains("=")) {
                    continue;
                }
                String comment;
                String key;
                String value;
                if (next.contains(";")) {
                    comment = next.split(";")[1].trim();
                    key = next.split(";")[0].split("=")[0].trim();
                    value = next.split(";")[0].split("=").length == 2 ? next.split(";")[0].split("=")[1].trim() : "";
                } else {
                    comment = "";
                    key = next.split("=")[0].trim();
                    value = next.split("=").length == 2 ? next.split("=")[1].trim() : "";
                }
                config.add(new String[]{key, value, comment});
            }
            configuration.put(f, headers);
        }
        return configuration;
    }

    public static void inputFile(File file, Type type) {
        inputFiles.put(type, file);
    }

    public static void parseFiles() {
        Arrays.stream(Type.values()).forEach(t -> InputParser.parse(t, inputFiles.get(t)));
        try {
            File newFile = new File("src\\data\\output");
            newFile.mkdir();
            generateFiles(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileInterface getOutput(String f) {
        return output.get(f);
    }

    public static void generateFiles(File folder) throws IOException {
        for (Map.Entry<String, FileInterface> e: output.entrySet()) {
            File output = new File(folder, e.getKey());
            output.createNewFile();
            PrintStream out = new PrintStream(output);
            e.getValue().writeFile(out);
            out.flush();
        }
    }

    public static String formatString(Type t) {
        switch (t) {
            case SUSPENSION:
                return "csv with connection vertices";
            case TORQUE:
                return "csv containing torque per RPM";
        }
        return null;
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
        Scanner in = null;
        try {
            in = new Scanner(prev);
        } catch (FileNotFoundException e) {
            return;
        }
        while (in.hasNextLine()) {
            String next = in.nextLine();
            inputFiles.put(Type.valueOf(next.split("=")[0]), new File(next.split("=")[1]));
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


    public enum Type {
        TORQUE, GEARS, AERO, SUSPENSION, NONE

    }
}
