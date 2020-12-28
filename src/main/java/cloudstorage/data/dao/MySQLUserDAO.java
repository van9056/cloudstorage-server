package cloudstorage.data.dao;

import cloudstorage.data.Database;
import cloudstorage.data.Role;
import cloudstorage.data.Storage;
import cloudstorage.data.User;
import org.apache.log4j.Logger;

import java.sql.*;

public class MySQLUserDAO implements UserDAO {

    private static final Logger logger = Logger.getLogger(MySQLUserDAO.class);
    private Connection connection = Database.getInstance().getConnection();
    private StorageDAO storageDAO;

    public MySQLUserDAO() {
        this.storageDAO = new MySQLStorageDAO();
    }

    @Override
    public synchronized User findByEmail(String email) {
        if (connection != null) {
            try {
                String query =
                                "select user.id," +
                                "user.username," +
                                "user.email," +
                                "user.password," +
                                "role.name as role," +
                                "user.id_storage " +
                                "from user inner join role on user.id_role=role.id " +
                                "where email=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("role").equalsIgnoreCase("USER")
                                    ? Role.USER
                                    : Role.ADMIN,
                            storageDAO.findById(resultSet.getInt("id_storage"))
                    );
                }
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public synchronized User create(String username, String email, String password, Role role, Storage storage) {
        if (findByEmail(email) == null) {
            if (connection != null) {
                try {
                    String query = "insert into user (username, email, password, id_role, id_storage) values (?,?,?,?,?)";
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, username);
                    statement.setString(2, email);
                    statement.setString(3, password);
                    statement.setInt(4, role.name().equalsIgnoreCase("ADMIN") ? 2 : 1);
                    statement.setInt(5, storage.getId());
                    statement.executeUpdate();

                    ResultSet resultSet = statement.getGeneratedKeys();
                    int id = 0;
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                    }
                    statement.close();
                    return new User(id, username, email, password, Role.USER, storage);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }
}
