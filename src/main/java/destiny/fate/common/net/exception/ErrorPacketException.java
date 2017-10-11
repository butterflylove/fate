package destiny.fate.common.net.exception;

/**
 * Created by zhangtianlong01 on 2017/10/11.
 */
public class ErrorPacketException extends RuntimeException {

    public ErrorPacketException() {
        super();
    }

    public ErrorPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorPacketException(String message) {
        super(message);
    }

    public ErrorPacketException(Throwable cause) {
        super(cause);
    }
}
