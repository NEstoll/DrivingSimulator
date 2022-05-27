package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LUTFile extends FileInterface{

    @Override
    public void writeFile(PrintStream out) {

    }

    public void addValue(String key, String value) {

    }

    @Override
    public void readFile(File in) throws FileNotFoundException {
        Scanner fileReader = new Scanner(in);
        while (fileReader.hasNextLine()) {
            String next = fileReader.nextLine();
            //TODO add values to data structure
        }
    }
}
