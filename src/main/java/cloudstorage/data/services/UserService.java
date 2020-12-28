package cloudstorage.data.services;

import cloudstorage.data.Role;
import cloudstorage.data.User;

public interface UserService {
    User create(String username, String email, String password, Role role);
    User findByEmail(String email);
}
