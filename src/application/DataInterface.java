package application;

import java.io.*;
import java.util.Scanner;

public class DataInterface {
    public File verifyAssetto() throws IOException { //see if Assetto is installed (only works on windows, but so does Assetto)
        //query registry for steam path
        InputStream is = Runtime.getRuntime().exec("reg query " + "\"HKEY_CURRENT_USER\\SOFTWARE\\Valve\\Steam\" /v SteamPath").getInputStream();
        StringWriter sw = new StringWriter();
        for (int c; (c = is.read()) != -1;)
            sw.write(c);
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
        return new File("");
    }

    public void generateConfigs() throws FileNotFoundException {
        File assetto = null;
        try {
            assetto = verifyAssetto();
        } catch (IOException e) {
            System.err.println("Assetto Corsa not found, please provide:");
            //add file import
        }
        //extract data.acd
        if (!(assetto = new File(assetto, "content\\cars\\abarth500\\data")).exists()) {
            return;
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
}
