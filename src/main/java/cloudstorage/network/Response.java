package cloudstorage.network;

public class Response {

    private ResponseType type;
    private String jsonBody;

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @Override
    public String toString() {
        if (jsonBody == null || jsonBody.isEmpty()) {
            return type.getHeader() + "\n";
        } else {
            return type.getHeader() + "\n" + jsonBody.length() + "\n" + jsonBody;
        }
    }
}
