package destiny.fate.common.net.handler.backend;

/**
 * 后端连接的状态枚举
 * @author zhangtianlong
 */
public interface BackendConnState {

    /**
     * 尚未初始化
     */
    int BACKEND_NOT_AUTHED = 0;

    /**
     * 初始化成功
     */
    int BACKEND_AUTHED = 1;

}
