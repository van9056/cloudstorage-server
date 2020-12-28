package cloudstorage.network;

import java.util.Map;

public class Request {

    private RequestType type;
    private Map<String, String> fields;

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
