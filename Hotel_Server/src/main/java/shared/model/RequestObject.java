package shared.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class RequestObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String action;
    private Object payload;
    private String requestId;
    private Instant timestamp;

    public RequestObject() {
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public RequestObject(String action, Object payload) {
        this();
        this.action = action;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
