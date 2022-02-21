package fileupload.fileupload.message;

import java.util.UUID;

public class ResponseMessage {
    private String uuid;
    private String message;
    public ResponseMessage(String uuid, String message) {
        this.uuid=uuid;
        this.message = message;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}