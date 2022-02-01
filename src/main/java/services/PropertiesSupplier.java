package services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesSupplier {
    private static final String propertiesRoot = "src/main/resources/application.properties";

    public static Properties getProperties(String root) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
