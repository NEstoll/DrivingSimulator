package application;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationFile {
    private Map<String, Map<String, String>> sections;

    public ConfigurationFile() {
        sections = new HashMap<>();
    }

    public void importData(Map<String, Map<String, String>> data) {
        sections = data;
    }

    public Map<String, String> getSection(String header) {
        return sections.get(header);
    }

    public String getValue(String header, String key) {
        return sections.get(header).get(key);
    }

    public void addSection(String header, Map<String, String> data) {
        sections.put(header, data);
    }

    public String setValue(String header, String key, String value) {
        return sections.get(header).put(key, value);
    }

    public static void writeFile(File f) {

    }

}
