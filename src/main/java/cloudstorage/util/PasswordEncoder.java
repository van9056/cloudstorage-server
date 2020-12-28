package cloudstorage.util;

import cloudstorage.ServerProperties;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Properties;

public class PasswordEncoder {

    private static Properties properties = ServerProperties.getProperties();
    private static String salt = properties.getProperty("globalSalt");

    public static String encode(String password) {
        if (salt != null && !salt.isEmpty()) {
            return hash(hash(password + salt));
        }
        return null;
    }

    private static String hash(String toHash) {
        return DigestUtils.sha256Hex(toHash);
    }
}
