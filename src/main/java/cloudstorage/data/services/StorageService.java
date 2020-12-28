package cloudstorage.data.services;

import cloudstorage.data.Storage;
import cloudstorage.data.dao.StorageDAO;

public interface StorageService {
    Storage createStorage(int storageSize);
    Storage createStorage();
}
