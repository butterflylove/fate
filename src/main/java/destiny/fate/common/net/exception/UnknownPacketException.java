package destiny.fate.common.net.exception;

/**
 * Created by zhangtianlong01 on 2017/10/11.
 */
public class UnknownPacketException extends RuntimeException {

    public UnknownPacketException() {
        super();
    }

    public UnknownPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPacketException(String message) {
        super(message);
    }

    public UnknownPacketException(Throwable cause) {
        super(cause);
    }
}
