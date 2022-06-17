package application;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;

public class INIFile extends FileInterface {
    private Map<String, Map<String, String>> sections;
    private String last;

    public INIFile() {
        sections = new HashMap<>();
    }

    public void setValue(String header, String key, String value) {

        try {
            Double d = Double.parseDouble(value);

            DecimalFormat roundDecimal = new DecimalFormat("#.#####");

            value = String.valueOf(roundDecimal.format(d));
            if (!sections.containsKey(header)) {
                sections.put(header, new HashMap<>());
            }
            sections.get(header).put(key, value);

        } catch (NumberFormatException e) {
            if (!sections.containsKey(header)) {
                sections.put(header, new HashMap<>());
            }
            sections.get(header).put(key, value);
        }
    }

    public Map<String, Map<String, String>> getValues() {
        return sections;
    }

    public void writeFile(PrintStream out) {
        for (Map.Entry<String, Map<String, String>> e: sections.entrySet()) {
            out.println(e.getKey());
            for (Map.Entry<String, String> line: e.getValue().entrySet()) {
                out.println(line.getKey() + '=' + line.getValue());
            }
        }
    }

    @Override
    public void readFile(File in) throws FileNotFoundException {
        Scanner fileReader = new Scanner(in);
        sections = new HashMap<>();
        Map<String, String> config = new HashMap<>();
        while (fileReader.hasNextLine()) {
            String next = fileReader.nextLine();
            if (next.startsWith("[") && next.endsWith("]")) {
                sections.put(next, (config = new HashMap<>()));
                continue;
            } else if (!next.contains("=")) {
                continue;
            }
            String key;
            String value;
            if (next.contains(";")) {
                key = next.split(";")[0].split("=")[0].trim();
                value = next.split(";")[0].split("=").length == 2 ? next.split(";")[0].split("=")[1].trim() : "";
            } else {
                key = next.split("=")[0].trim();
                value = next.split("=").length == 2 ? next.split("=")[1].trim() : "";
            }
            config.put(key, value);
        }
    }

}
