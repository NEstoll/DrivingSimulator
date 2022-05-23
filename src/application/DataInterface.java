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
    private static final DataInterface singleton = new DataInterface();
    private Map<String, File> inputFiles;

    private DataInterface() {
        inputFiles = new HashMap<>();
    }

    public static DataInterface getInstance() {
        return singleton;
    }

    public Map<String, File> getInputFiles() {
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
                return library;
            }

        }
        //none found, return empty file
        throw new FileNotFoundException("Assetto Corsa not found");
    }


    /**
     * @param car the name of the car/folder to read the configuration files from
     * @throws FileNotFoundException if data folder not found/not extracted
     */
    public void generateConfigs(String car) throws FileNotFoundException {
        File assetto;
        try {
            assetto = verifyAssetto();
        } catch (IOException e) {
            System.err.println("Assetto Corsa not found, please provide:");
            //add file import
            return;
        }
        //extract data.acd
        if (!(assetto = new File(assetto, "content\\cars\\" + car + "\\data")).exists()) {
            throw new FileNotFoundException("data folder not found");
        }
        Scanner reader = new Scanner(System.in);
        for (File f: assetto.listFiles()) {
            System.out.println(f.getName());
            Scanner fileReader = new Scanner(f);
            while (fileReader.hasNextLine()) {
                String next = fileReader.nextLine();
                if (next.startsWith("[") && next.endsWith("]") || !next.contains(";")) {
                    continue;
                }
                System.out.println(next.split(";")[1]);
                System.out.print(next.split(";")[0].split("=")[0] + "=");
                System.out.println('"' + reader.nextLine() + "\" added to config");
            }
        }
    }

    public void inputFile(File file, String type) {
        inputFiles.put(type, file);
    }

    public void generateFiles() {

    }
}
