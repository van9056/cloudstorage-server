package cloudstorage.data;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.util.Random;

public class Session {

    private static final long DEFAULT_TTL = 1800; // seconds

    private String key;             // Hex-value
    private long timeToLive;        // seconds
    private long timeOfCreation;    // seconds

    public Session(long timeToLive) {
        this.key = generateKey();
        this.timeToLive = timeToLive;
        this.timeOfCreation = System.currentTimeMillis()/1000;
    }

    public Session() {
        this(DEFAULT_TTL);
    }

    private String generateKey() {
        return DigestUtils.sha512Hex(String.valueOf(new Random(System.currentTimeMillis()).nextInt()));
    }

    public String getKey() {
        return key;
    }

    public boolean isAlive() {
        long currentTime = System.currentTimeMillis()/1000;
        return (currentTime - timeOfCreation) <= timeToLive;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object instanceof Session) {
            Session session = (Session) object;
            return session.key.equalsIgnoreCase(this.key);
        } else if (object instanceof String) {
            String session = (String) object;
            return session.equalsIgnoreCase(this.key);
        } else return false;
    }
}
