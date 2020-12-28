package cloudstorage.data.services;

import cloudstorage.data.User;

public interface AuthenticationService {
    User authenticate(String email, String password);
    User authenticate(String session);
    User register(String username, String email, String password);
    SessionManager getSessionManager();
    //IStorageService getStorageService();
}
