package cloudstorage;

import cloudstorage.data.Database;
import cloudstorage.network.CloudServer;
import org.apache.log4j.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    private static Database db = Database.getInstance();

    public static void main(String[] args) throws Exception {

        logger.info("Подготовка к запуску");
        if (!db.openConnection()) {
            logger.error("Не удалось подключиться к базе данных!");
            System.exit(1);
        }

        CloudServer server = new CloudServer();
        Thread serverThread = server.start();
        logger.info("Сервер успешно запущен");
        serverThread.join();

        db.closeConnection();
    }
}
