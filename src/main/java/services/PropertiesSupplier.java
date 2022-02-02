package services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesSupplier {
    private static final String propertiesRoot = "/application.properties";

    public Properties getProperties() {
        Properties properties = new Properties();
        try {
            InputStream stream = getClass().getResourceAsStream(propertiesRoot);
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
