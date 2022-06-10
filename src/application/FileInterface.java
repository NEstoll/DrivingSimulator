package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public abstract class FileInterface {
    public abstract void writeFile(PrintStream out);

    public abstract void readFile(File in) throws FileNotFoundException;
}
