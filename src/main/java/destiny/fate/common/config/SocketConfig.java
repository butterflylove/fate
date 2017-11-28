package destiny.fate.common.config;

/**
 * @author zhangtianlong
 */
public interface SocketConfig {

    int CONNECT_TIMEOUT_MILLIS = 5000;

    int SO_TIMEOUT = 10 * 60;

    int BACKEND_SOCKET_RECV_BUF = 4 * 1024 * 1024;

    int BACKEND_SOCKET_SEND_BUF = 1024 * 1024;

}
