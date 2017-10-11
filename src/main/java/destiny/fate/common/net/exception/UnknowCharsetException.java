package destiny.fate.common.net.exception;

/**
 * Created by zhangtianlong01 on 2017/10/11.
 */
public class UnknowCharsetException extends RuntimeException {

    public UnknowCharsetException() {
        super();
    }

    public UnknowCharsetException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknowCharsetException(String message) {
        super(message);
    }

    public UnknowCharsetException(Throwable cause) {
        super(cause);
    }
}
