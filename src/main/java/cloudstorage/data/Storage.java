package cloudstorage.data;

import cloudstorage.ServerProperties;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

public class Storage {

    private static final Logger logger = Logger.getLogger(Storage.class);
    private static Properties properties = ServerProperties.getProperties();

    private int id;
    private long totalSize;
    private long size;
    private Path root;
    private FileProvider fileProvider;

    public Storage(int id, int totalSize) {
        this.id = id;
        this.totalSize = totalSize;
        this.root = Paths.get(properties.getProperty("storage.rootPath"), String.valueOf(id));
        this.fileProvider = new FileProvider();
        updateSize();
    }

    public int getId() {
        return id;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getSize() {
        return size;
    }

    public FileProvider getFileProvider() {
        return fileProvider;
    }

    public class FileProvider {

        public void upload(Path relativePath, InputStream is, long length) {
            try {
                if (!canUpload(length)) throw new Exception("Can't upload this file, too big!");
                Path path = preparePath(relativePath);
                if (!Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS)) {
                    Files.createDirectories(path.getParent());
                }
                FileOutputStream fos = new FileOutputStream(path.toFile());
                long completed = 0;
                while (completed < length) {
                    byte[] bytes = is.readAllBytes();
                    completed += bytes.length;
                    fos.write(bytes);
                }
                fos.close();
                updateSize();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        public void download(Path relativePath, OutputStream os) {
            try {
                Path path = preparePath(relativePath);
                if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                    FileInputStream fis = new FileInputStream(path.toFile());
                    os.write(fis.readAllBytes());
                    fis.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        public boolean delete(Path relativePath) {
            try {
                Path path = preparePath(relativePath);
                if (Files.deleteIfExists(path)) {
                    updateSize();
                    return true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return false;
        }

        public JSONObject getFileStructureAsJson(Path relativePath) {
            try {
                Path path = preparePath(relativePath);
                File[] content = path.toFile().listFiles();
                JSONArray jsonArray = new JSONArray();
                for (File file : content) {
                    JSONObject c = new JSONObject();
                    if (file.isDirectory()) {
                        c.put("name", file.getName());
                    } else if (file.isFile()) {
                        c.put("name", file.getName());
                        c.put("size", file.length());
                    }
                    jsonArray.put(c);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("structure", jsonArray);
                return jsonObject;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return null;
        }

        public int getFileSize(Path relativePath) {
            try {
                Path path = preparePath(relativePath);
                return (int) Files.size(path);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return 0;
        }

        public boolean fileExists(Path relativePath) {
            if (validatePath(relativePath)) {
                try {
                    Path path = preparePath(relativePath);
                    return Files.exists(path);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            return false;
        }

        public boolean canUpload(long size) {
            return (Storage.this.totalSize - Storage.this.size) >= size;
        }

        private Path preparePath(Path relativePath) throws Exception {
            Path path = relativePath.normalize();
            if (validatePath(path)) {
                Path result = Paths.get(root.toString(), path.toString()).normalize();
                if (result.startsWith(root)) return result;
            }
            throw new Exception("Invalid path (" + path + ")");
        }

        private boolean validatePath(Path path) {
            if (path != null) {
                if (!path.isAbsolute())
                    return true;
            }
            return false;
        }
    }

    public void updateSize() {
        try {
            if (Files.notExists(root)) Files.createDirectories(root);
            size = 0;
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
                        size += file.toFile().length(); // Выяснить, возможно ли использовать тут Files.size(...) ?
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}