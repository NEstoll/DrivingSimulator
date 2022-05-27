package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class RTOFile extends LUTFile{
    public RTOFile() {
        super();
    }

    @Override
    public void writeFile(PrintStream out) {
        super.writeFile(out);
    }

    @Override
    public void addValue(String key, String value) {
        super.addValue(key, value);
    }

    @Override
    public void readFile(File in) throws FileNotFoundException {
    }
}
