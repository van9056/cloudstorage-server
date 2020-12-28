package cloudstorage.network;

public enum ResponseType {
    OK("OK CTCP"), BAD("BAD CTCP");

    private String header;

    ResponseType(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}