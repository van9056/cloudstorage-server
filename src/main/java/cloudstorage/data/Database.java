package cloudstorage.data;

import cloudstorage.ServerProperties;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public class Database {

    private static final Logger logger = Logger.getLogger(Database.class);

    private static volatile Database instance;

    private static Properties properties = ServerProperties.getProperties();
    private static String url = "jdbc:mysql://" +
                    properties.getProperty("database.host") + ":" +
                    properties.getProperty("database.port") + "/" +
                    properties.getProperty("database.name");
    private static String user = properties.getProperty("database.user");
    private static String password = properties.getProperty("database.password");

    private static Connection connection;

    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public synchronized boolean openConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public synchronized void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        try {
            if (connection != null && !connection.isClosed())
                return true;
        } finally {
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
