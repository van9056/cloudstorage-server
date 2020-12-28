package cloudstorage.data.services;

import cloudstorage.data.Session;
import cloudstorage.data.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static Map<User, Session> sessions = new HashMap<>();

    public static void put(User user, Session session) {
        sessions.put(user, session);
    }

    public static User get(String session) {
        for (User user : sessions.keySet()) {
            Session s = sessions.get(user);
            if (s.getKey().equalsIgnoreCase(session)) {
                if (!s.isAlive()) {
                    sessions.remove(user);
                    return null;
                }
                return user;
            }
        }
        return null;
    }

    public static Session get(User user) {
        return sessions.get(user);
    }
}
