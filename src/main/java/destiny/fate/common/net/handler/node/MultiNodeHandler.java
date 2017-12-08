package destiny.fate.common.net.handler.node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangtianlong
 */
public abstract class MultiNodeHandler implements ResponseHandler {
    /**
     * 执行节点数量
     */
    private int nodeCount;
    /**
     * 并发请求,执行节点锁
     */
    protected final ReentrantLock lock = new ReentrantLock();

    protected byte packetId;

    protected int errno;

    protected String errorMessage;
    /**
     * 是否已经失败了
     */
    protected AtomicBoolean isFailed = new AtomicBoolean(false);

    protected void reset(int initCount) {
        this.nodeCount = initCount;
        this.isFailed.set(false);
        errorMessage = null;
        packetId = 0;
    }

    protected boolean decrementNodeCount() {
        boolean zeroReached = false;
        --nodeCount;
        if (nodeCount == 0) {
            zeroReached = true;
        }
        return zeroReached;
    }

    protected void setFailMsg(String errorMessage) {
        isFailed.set(true);
        this.errorMessage = errorMessage;
    }
}
