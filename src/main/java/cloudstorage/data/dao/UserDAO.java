package cloudstorage.data.dao;

import cloudstorage.data.Role;
import cloudstorage.data.Storage;
import cloudstorage.data.User;

public interface UserDAO {
    User findByEmail(String email);
    User create(String username, String email, String password, Role role, Storage storage);
}
