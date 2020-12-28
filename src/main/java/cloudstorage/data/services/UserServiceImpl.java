package cloudstorage.data.services;

import cloudstorage.data.Role;
import cloudstorage.data.User;
import cloudstorage.data.dao.UserDAO;
import cloudstorage.data.dao.MySQLUserDAO;
import cloudstorage.util.PasswordEncoder;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private StorageService storageService;

    public UserServiceImpl() {
        this.userDAO = new MySQLUserDAO();
        this.storageService = new StorageServiceImpl();
    }

    @Override
    public User create(String username, String email, String password, Role role) {
        User user = userDAO.create(username, email, PasswordEncoder.encode(password), role, storageService.createStorage());
        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }
}
