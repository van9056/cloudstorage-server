package cloudstorage.network;

public enum RequestType {
    REGISTRATION("REG CTCP"),
    AUTHENTICATION("AUTH CTCP"),
    INFORMATION("INFO CTCP"),
    UPLOAD("UPLOAD CTCP"),
    DOWNLOAD("DOWNLOAD CTCP"),
    DELETE("DELETE CTCP");

    private String header;

    RequestType(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
