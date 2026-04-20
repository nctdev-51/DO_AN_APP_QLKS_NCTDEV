package shared.model;

import java.io.Serializable;

public class ResponseObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Object data;

    public ResponseObject() {
    }

    public ResponseObject(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static ResponseObject ok(String message, Object data) {
        return new ResponseObject(true, message, data);
    }

    public static ResponseObject fail(String message) {
        return new ResponseObject(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
