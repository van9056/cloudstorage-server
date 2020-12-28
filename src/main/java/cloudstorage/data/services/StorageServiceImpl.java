package cloudstorage.data.services;

import cloudstorage.ServerProperties;
import cloudstorage.data.Storage;
import cloudstorage.data.dao.StorageDAO;
import cloudstorage.data.dao.MySQLStorageDAO;

public class StorageServiceImpl implements StorageService {

    private StorageDAO storageDAO = new MySQLStorageDAO();
    private int defaultStorageSize =
            Integer.parseInt(ServerProperties.getProperties().getProperty("storage.defaultSize"));

    @Override
    public Storage createStorage(int storageSize) {
        return storageDAO.create(storageSize);
    }

    @Override
    public Storage createStorage() {
        return storageDAO.create(defaultStorageSize);
    }
}
