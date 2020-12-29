package cloudstorage;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerProperties {

    private static final Logger logger = Logger.getLogger(ServerProperties.class);
    private static Properties properties = loadProperties();

    private static Properties loadProperties() {
        File file = Paths.get("src\\main\\resources\\application.properties").toFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(fis);
            logger.info("Загружена основная конфигурация");
            return properties;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
