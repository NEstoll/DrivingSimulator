package application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;

public class Program {
    public File verifyAssetto() throws IOException {
        InputStream is = Runtime.getRuntime().exec("reg query " + "\"HKEY_CURRENT_USER\\SOFTWARE\\Valve\\Steam\" /v SteamPath").getInputStream();
        StringWriter sw = new StringWriter();
        for (int c; (c = is.read()) != -1;)
            sw.write(c);
        File libraries = new File(sw.toString().substring(sw.toString().indexOf("REG_SZ    ") + "REG_SZ    ".length()).trim()+"\\steamapps\\libraryfolders.vdf");
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
        return new File("");
    }
}
