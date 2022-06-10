package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LUTFile extends FileInterface{

    ArrayList<Pair<String,String>> lutData;

    public LUTFile() {
        lutData = new ArrayList<>();
    }

    @Override
    public void writeFile(PrintStream out) {
        if (lutData.size() > 0) {
            for (int i = 0; i < lutData.size(); i++) {
                Pair<String, String> pair = lutData.get(i);
                out.println(pair.key + "|" + pair.value);
            }
        }
    }

    public ArrayList<Pair<String, String>> getInputData() {
        return lutData;
    }

    public void clear() {
        lutData.clear();
    }

    // Assumes sorted input data, maintains a sort on lutData for future output
    public void addValue(String key, String value) {
        Double.parseDouble(key);
        Double.parseDouble(value);
        for (int i = 0; i < lutData.size(); i++) {
            Pair<String, String> pair = lutData.get(i);
            // Update existing entry if applicable
            if (pair.key.equals(key)) {
                pair.value = value;
                return;
            }
            // Add a new entry if not present in data
            if (Double.parseDouble(pair.key) > Double.parseDouble(key)) {
                lutData.add(i, new Pair<>(key, value));
                return;
            }
        }
        lutData.add(new Pair<>(key,value));
    }

    @Override
    public void readFile(File in) throws FileNotFoundException {
        Scanner fileReader = new Scanner(in);
        while (fileReader.hasNextLine()) {
            String next = fileReader.nextLine();
            if (next.isEmpty()) {
                continue;
            }
            //TODO add values to data structure
            String[] rpmTorque = next.split("\\|");
            if (rpmTorque.length != 2) {
                throw new NumberFormatException();
            }
            lutData.add(new Pair<>(rpmTorque[0], rpmTorque[1]));
        }
        return;
    }
}
