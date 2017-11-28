package destiny.fate.common.net.handler.node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangtianlong01 on 2017/11/28.
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

}
