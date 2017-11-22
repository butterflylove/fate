package destiny.fate.common.net.exception;

/**
 * Created by zhangtianlong01 on 2017/11/22.
 */
public class RetryConnectFailException extends RuntimeException {

    public RetryConnectFailException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RetryConnectFailException(String msg) {
        super(msg);
    }
}
