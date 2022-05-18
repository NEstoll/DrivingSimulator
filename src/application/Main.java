package application;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        try {
            new Program().verifyAssetto();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int other() {
        return 2;
    }
}
