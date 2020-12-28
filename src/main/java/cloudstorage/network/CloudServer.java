package cloudstorage.network;

import cloudstorage.ServerProperties;
import cloudstorage.data.dao.MySQLStorageDAO;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Properties;

public class CloudServer {

    private static final Logger logger = Logger.getLogger(CloudServer.class);
    private static Properties properties = ServerProperties.getProperties();

    private ServerSocket serverSocket;
    private boolean isActive = false;
    private int timeout = 30; // seconds


    public CloudServer() {
        try {
            int port = Integer.parseInt(properties.getProperty("server.port"));
            InetAddress inetAddress = InetAddress.getByName(properties.getProperty("server.ip"));
            this.serverSocket = new ServerSocket(port, 50, inetAddress);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public Thread start() {
        isActive = true;
        Thread currentThread = new Thread(() -> {
            while (isActive) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(1000 * timeout);
                    new Thread(new CloudListener(socket)).start();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        });
        currentThread.start();
        return currentThread;
    }

    public void stop() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }
}
