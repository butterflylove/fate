package destiny.fate.common.net.handler.session;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.frontend.FrontendConnection;
import destiny.fate.common.net.route.RouteResultsetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;


/**
 * 前端会话过程
 *
 * @author zhangtianlong
 */
public class FrontendSession implements Session {

    private static final Logger logger = LoggerFactory.getLogger(FrontendSession.class);

    private FrontendConnection source;

    /**
     * 事务是否被打断
     */
    private volatile boolean txInterrupted;

    /**
     * 事务被打断保存的信息
     */
    private volatile String txInterruptMsg = "";

    private final ConcurrentHashMap<RouteResultsetNode, BackendConnection> target;

    public FrontendSession(FrontendConnection source) {
        this.source = source;
        target = new ConcurrentHashMap<RouteResultsetNode, BackendConnection>();
    }

    public void writeErrMsg(int errNo, String msg) {
        logger.warn(String.format("[FrontendConnection]ErrorNo=%d, ErrorMsg=%s", errNo, msg));
        source.writeErrMessage((byte) 1, errNo, msg);
    }

    public FrontendConnection getSource() {
        return null;
    }

    public int getTargetCount() {
        return 0;
    }

    public void execute(String sql, int type) {

    }

    public void commit() {

    }

    public void rollback() {

    }

    public void cancel(FrontendConnection sponsor) {

    }

    public void terminate() {

    }

    public void close() {

    }

    public BackendConnection getTarget(RouteResultsetNode key) {
        BackendConnection backend = target.get(key);
        if (backend == null) {
            backend = source.getStateSyncBackend();
            target.put(key, backend);
        }
        return backend;
    }
}
