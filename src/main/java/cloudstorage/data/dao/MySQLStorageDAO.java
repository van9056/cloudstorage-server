package cloudstorage.data.dao;

import cloudstorage.data.Database;
import cloudstorage.data.Storage;
import org.apache.log4j.Logger;

import java.sql.*;

public class MySQLStorageDAO implements StorageDAO {

    private static final Logger logger = Logger.getLogger(MySQLStorageDAO.class);
    private Connection connection = Database.getInstance().getConnection();

    @Override
    public synchronized Storage findById(int storageId) {
        try {
            String query = "select size from storage where id=" + storageId;
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.next();
            Storage storage = new Storage(
                    storageId,
                    result.getInt("size")
            );
            return storage;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public synchronized Storage create(int size) {
        try {
            String query = "insert into storage (size) values (" + size + ")";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            Storage storage = new Storage(result.getInt(1), size);
            return storage;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
