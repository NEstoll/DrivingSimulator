package application;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Data class, will handle reading and writing all data to and from files, as well as any modifications that are needed.
 */
public class DataInterface {
    private static Map<Type, File> inputFiles = new HashMap<>();
    private static File assetto;

    public static Map<Type, File> getInputFiles() {
        return inputFiles;
    }

    /**
     * Verifies that Assetto Corsa is installed on the computer, and returns the location of the install if found. Relies on the windows registry, will not work on other systems.
     *
     * @return a File pointing to the top level folder of the Assetto Corsa installation
     * @throws IOException if registry query fails
     * @throws FileNotFoundException if Assetto Corsa isn't found
     */
    public static File verifyAssetto() throws IOException, FileNotFoundException { //see if Assetto is installed (only works on windows, but so does Assetto)
        if (assetto != null) {
            return assetto;
        }
        //query registry for steam path
        InputStream is = Runtime.getRuntime().exec("reg query " + "\"HKEY_CURRENT_USER\\SOFTWARE\\Valve\\Steam\" /v SteamPath").getInputStream();
        StringWriter sw = new StringWriter();
        for (int c; (c = is.read()) != -1;)
            sw.write(c);
        if (sw.toString().equals("")) {
            throw new IOException();
        }
        File libraries = new File(sw.toString().substring(sw.toString().indexOf("REG_SZ    ") + "REG_SZ    ".length()).trim()+"\\steamapps\\libraryfolders.vdf");
        //read all of the libraries (places where steam stores files)
        Scanner reader = new Scanner(libraries);
        String next;
        while (reader.hasNextLine()) {
            if (!(next = reader.nextLine()).contains("path")) {
                continue;
            }
            File library = new File(next.replaceAll(".*\"(.+?)\".*", "$1") + "\\steamapps\\common\\assettocorsa");
            if (library.exists()) {
                assetto = library;
                return library;
            }

        }
        //none found, return empty file
        throw new FileNotFoundException("Assetto Corsa not found");
    }

    /**
     * @param assetto The assetocorsa folder containing all of the game's data and files
     */
    public static void setAssetto(File assetto) {
        DataInterface.assetto = assetto;
    }

    /**
     * @param car the name of the car/folder to read the configuration files from
     * @throws FileNotFoundException if data folder not found/not extracted
     */
    public static Map<File, ArrayList<String[]>> generateConfigs(String car) throws FileNotFoundException {
        try {
            assetto = verifyAssetto();
        } catch (IOException e) {
            System.err.println("Assetto Corsa not found, please provide:");
            //add file import
            return null;
        }
        //extract data.acd
        if (!(assetto = new File(assetto, "content\\cars\\" + car + "\\data")).exists()) {
            throw new FileNotFoundException("data folder not found");
        }
        Map<File, ArrayList<String[]>> configuration = new HashMap<>();
        for (File f: assetto.listFiles()) {
            if (!f.getName().endsWith(".ini")) {
                continue;
            }
            Scanner fileReader = new Scanner(f);
            ArrayList<String[]> config = new ArrayList<>();
            while (fileReader.hasNextLine()) {
                String next = fileReader.nextLine();
                if (next.startsWith("[") && next.endsWith("]")) {
                    config.add(new String[]{next});
                    continue;
                } else if (!next.contains("=")) {
                    continue;
                }
                String comment;
                String key;
                String value;
                if (next.contains(";")) {
                    comment = next.split(";")[1];
                    key = next.split(";")[0].split("=")[0];
                    value = next.split(";")[0].split("=").length==2?next.split(";")[0].split("=")[1]:"";
                } else {
                    comment = "";
                    key = next.split("=")[0];
                    value = next.split("=").length==2?next.split("=")[1]:"";
                }
                config.add(new String[]{key, comment, value});
            }
            configuration.put(f, config);
        }
        return configuration;
    }

    public static void inputFile(File file, Type type) {
        inputFiles.put(type, file);
    }

    public static void generateFiles(File folder) {

    }


    public enum Type {
        POWER, GEARS, AERO, SUSPENSION

    }
}
