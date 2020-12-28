package cloudstorage.data.services;

import cloudstorage.data.Role;
import cloudstorage.data.Session;
import cloudstorage.data.User;
import cloudstorage.util.PasswordEncoder;

public class AuthenticationServiceImpl implements AuthenticationService {

    private UserService userService;

    private SessionManager sessionManager;

    public AuthenticationServiceImpl() {
        this.userService = new UserServiceImpl();
        this.sessionManager = new SessionManager();
    }

    public synchronized User authenticate(String email, String password) {
        User findUser = userService.findByEmail(email);
        if (findUser != null) {
            if (PasswordEncoder.encode(password).equalsIgnoreCase(findUser.getPassword())) {
                Session session = new Session();
                sessionManager.put(findUser, session);
                return findUser;
            }
        }
        return null;
    }

    public synchronized User authenticate(String session) {
        return sessionManager.get(session);
    }

    public synchronized User register(String username, String email, String password) {
        return userService.create(username, email, password, Role.USER);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
