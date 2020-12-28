package cloudstorage.network;

import cloudstorage.data.User;
import cloudstorage.data.Storage;
import cloudstorage.data.services.AuthenticationServiceImpl;
import cloudstorage.data.services.AuthenticationService;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CloudListener implements Runnable {

    private static final Logger logger = Logger.getLogger(CloudListener.class);

    private final String REGISTRATION_HEADER = "REG CTCP";
    private final String AUTHENTICATION_HEADER = "AUTH CTCP";
    private final String INFORMATION_HEADER = "INFO CTCP";
    private final String UPLOAD_HEADER = "UPLOAD CTCP";
    private final String DOWNLOAD_HEADER = "DOWNLOAD CTCP";
    private final String DELETE_HEADER = "DELETE CTCP";

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private BufferedReader reader;
    private BufferedWriter writer;

    public CloudListener(Socket socket) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            reader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            writer = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            logger.info(socket.getInetAddress().getHostAddress() + " подключился");
            while (true) {
                String header = reader.readLine();
                if (header == null) break;
                switch (header) {
                    case REGISTRATION_HEADER:
                        logger.info(socket.getInetAddress().getHostAddress() + " запросил регистрацию");
                        reg();
                        break;
                    case AUTHENTICATION_HEADER:
                        logger.info(socket.getInetAddress().getHostAddress() + " запросил аутентификацию");
                        auth();
                        break;
                    case UPLOAD_HEADER:
                        logger.info(socket.getInetAddress().getHostAddress() + " запросил загрузку файла");
                        upload();
                        break;
                    case DOWNLOAD_HEADER:
                        logger.info(socket.getInetAddress().getHostAddress() + " запросил скачивание файла");
                        download();
                        break;
                    case INFORMATION_HEADER:
                        logger.info(socket.getInetAddress().getHostAddress() + " запросил информацию");
                        info();
                        break;
                    case DELETE_HEADER:
                        logger.info(socket.getInetAddress().getHostAddress() + " запросил удаление файла");
                        delete();
                        break;
                    default:
                        socket.close();
                }
            }
        } catch (SocketException e) { // ignore
        } catch (SocketTimeoutException e) { // ignore
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                reader.close();
                writer.close();
                socket.close();
                logger.info(socket.getInetAddress().getHostAddress() + " отключился");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private Request parseRegistrationRequest(JSONObject jsonObject) {
        Request request = new Request();
        Map<String, String> fields = new HashMap<>();
        fields.put("username", jsonObject.getString("username"));
        fields.put("email", jsonObject.getString("email"));
        fields.put("password", jsonObject.getString("password"));
        request.setFields(fields);
        return request;
    }

    private void reg() throws Exception {
        int length = Integer.parseInt(reader.readLine());
        char[] buffer = new char[length];
        if (reader.read(buffer) < length)
            throw new Exception("Буфер не заполнился до конца! Ошибка получения Json файла");
        String json = new String(buffer);
        Request request = parseRegistrationRequest(new JSONObject(json));
        Response response = new Response();
        Map<String, String> fields = request.getFields();
        String username = fields.get("username");
        String email = fields.get("email");
        String password = fields.get("password");
        AuthenticationService authService = new AuthenticationServiceImpl();
        User user = authService.register(username, email, password);
        if (user != null) {
            response.setType(ResponseType.OK);
        } else {
            response.setType(ResponseType.BAD);
        }
        writer.write(response.toString());
        writer.flush();
    }

    private Request parseAuthenticationRequest(JSONObject jsonObject) {
        Request request = new Request();
        Map<String, String> fields = new HashMap<>();
        fields.put("email", jsonObject.getString("email"));
        fields.put("password", jsonObject.getString("password"));
        request.setFields(fields);
        return request;
    }

    private void auth() throws Exception {
        int length = Integer.parseInt(reader.readLine());
        char[] buffer = new char[length];
        if (reader.read(buffer) < length)
            throw new Exception("Буфер не заполнился до конца! Ошибка получения Json файла");
        String json = new String(buffer);
        Request request = parseAuthenticationRequest(new JSONObject(json));
        Response response = new Response();
        Map<String, String> fields = request.getFields();
        String email = fields.get("email");
        String password = fields.get("password");
        AuthenticationService authService = new AuthenticationServiceImpl();
        User user = authService.authenticate(email, password);
        if (user != null) {
            response.setType(ResponseType.OK);
            JSONObject jsonObject = new JSONObject();
            String sessionKey = authService.getSessionManager().get(user).getKey();
            jsonObject.put("session", sessionKey);
            response.setJsonBody(jsonObject.toString());
        } else {
            response.setType(ResponseType.BAD);
        }
        writer.write(response.toString());
        writer.flush();
    }

    private Request parseUploadRequest(JSONObject jsonObject) {
        Request request = new Request();
        Map<String, String> fields = new HashMap<>();
        fields.put("session", jsonObject.getString("session"));
        fields.put("path", jsonObject.getString("path"));
        fields.put("size", jsonObject.getString("size"));
        request.setFields(fields);
        return request;
    }

    private void upload() throws Exception {
        int length = Integer.parseInt(reader.readLine());
        char[] buffer = new char[length];
        if (reader.read(buffer) < length)
            throw new Exception("Буфер не заполнился до конца! Ошибка получения Json файла");
        String json = new String(buffer);
        Request request = parseUploadRequest(new JSONObject(json));
        Response response = new Response();
        Map<String, String> fields = request.getFields();
        String session = fields.get("session");
        Path path = Paths.get(fields.get("path"));
        int size = Integer.parseInt(fields.get("size"));
        AuthenticationService authService = new AuthenticationServiceImpl();
        User user = authService.authenticate(session);
        Storage.FileProvider fileProvider = user.getStorage().getFileProvider();
        if (user != null && fileProvider.canUpload(size)) {
            user.getStorage().getFileProvider().upload(path, bufferedInputStream, size);
            response.setType(ResponseType.OK);
        } else {
            response.setType(ResponseType.BAD);
        }
        writer.write(response.toString());
        writer.flush();
    }

    private Request parseDownloadRequest(JSONObject jsonObject) {
        Request request = new Request();
        Map<String, String> fields = new HashMap<>();
        fields.put("session", jsonObject.getString("session"));
        fields.put("path", jsonObject.getString("path"));
        request.setFields(fields);
        return request;
    }

    private void download() throws Exception {
        int length = Integer.parseInt(reader.readLine());
        char[] buffer = new char[length];
        if (reader.read(buffer) < length)
            throw new Exception("Буфер не заполнился до конца! Ошибка получения Json файла");
        String json = new String(buffer);
        Request request = parseDownloadRequest(new JSONObject(json));
        Response response = new Response();
        Map<String, String> fields = request.getFields();
        String session = fields.get("session");
        Path path = Paths.get(fields.get("path"));
        AuthenticationService authService = new AuthenticationServiceImpl();
        User user = authService.authenticate(session);
        Storage.FileProvider fileProvider = user.getStorage().getFileProvider();
        if (user != null && fileProvider.fileExists(path)) {
            response.setType(ResponseType.OK);
            JSONObject jsonObject = new JSONObject();
            int fileSize = fileProvider.getFileSize(path);
            jsonObject.put("size", fileSize);
            response.setJsonBody(jsonObject.toString());
            writer.write(response.toString());
            writer.flush();
            user.getStorage().getFileProvider().download(path, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } else {
            response.setType(ResponseType.BAD);
            writer.write(response.toString());
            writer.flush();
        }
    }

    private Request parseInformationRequest(JSONObject jsonObject) {
        Request request = new Request();
        Map<String, String> fields = new HashMap<>();
        fields.put("session", jsonObject.getString("session"));
        fields.put("path", jsonObject.getString("path"));
        request.setFields(fields);
        return request;
    }

    private void info() throws Exception {
        int length = Integer.parseInt(reader.readLine());
        char[] buffer = new char[length];
        if (reader.read(buffer) < length)
            throw new Exception("Буфер не заполнился до конца! Ошибка получения Json файла");
        String json = new String(buffer);
        Request request = parseInformationRequest(new JSONObject(json));
        Response response = new Response();
        Map<String, String> fields = request.getFields();
        String session = fields.get("session");
        Path path = Paths.get(fields.get("path"));
        AuthenticationService authService = new AuthenticationServiceImpl();
        User user = authService.authenticate(session);
        Storage.FileProvider fileProvider = user.getStorage().getFileProvider();
        JSONObject jsonObject;
        if (user != null && fileProvider.fileExists(path)
                && (jsonObject = fileProvider.getFileStructureAsJson(path)) != null) {
            response.setType(ResponseType.OK);
            response.setJsonBody(jsonObject.toString());
        } else {
            response.setType(ResponseType.BAD);
        }
        writer.write(response.toString());
        writer.flush();
    }

    private Request parseDeleteRequest(JSONObject jsonObject) {
        Request request = new Request();
        Map<String, String> fields = new HashMap<>();
        fields.put("session", jsonObject.getString("session"));
        fields.put("path", jsonObject.getString("path"));
        request.setFields(fields);
        return request;
    }

    private void delete() throws Exception {
        int length = Integer.parseInt(reader.readLine());
        char[] buffer = new char[length];
        if (reader.read(buffer) < length)
            throw new Exception("Буфер не заполнился до конца! Ошибка получения Json файла");
        String json = new String(buffer);
        Request request = parseDeleteRequest(new JSONObject(json));
        Response response = new Response();
        Map<String, String> fields = request.getFields();
        String session = fields.get("session");
        Path path = Paths.get(fields.get("path"));
        AuthenticationService authService = new AuthenticationServiceImpl();
        User user = authService.authenticate(session);
        Storage.FileProvider fileProvider = user.getStorage().getFileProvider();
        if (user != null && fileProvider.delete(path)) {
            response.setType(ResponseType.OK);
        } else {
            response.setType(ResponseType.BAD);
        }
        writer.write(response.toString());
        writer.flush();
    }
}
