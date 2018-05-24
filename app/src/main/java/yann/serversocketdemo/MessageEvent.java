package yann.serversocketdemo;

/**
 * Created by yayun.xia on 2018/5/24.
 */

public class MessageEvent {

    private String message;

    private Object object;

    public MessageEvent(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
