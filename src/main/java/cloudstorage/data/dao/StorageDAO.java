package cloudstorage.data.dao;

import cloudstorage.data.Storage;

public interface StorageDAO {
    Storage findById(int storageId);
    Storage create(int size);
}
