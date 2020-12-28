package cloudstorage;

import cloudstorage.data.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerProperties {

    private static Properties properties = loadProperties();

    private static Properties loadProperties() {
        File file = Paths.get("src\\main\\resources\\application.properties").toFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(fis);
            return properties;
        } catch (IOException e) {
            System.err.println("[CloudStorage] Ошибка: конфигурационный файл не был загружен!");
            return null;
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
