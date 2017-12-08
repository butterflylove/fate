package destiny.fate.common.net.handler.node;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.ErrorPacket;
import destiny.fate.common.net.protocol.OkPacket;
import destiny.fate.common.net.route.RouteResultset;
import destiny.fate.common.net.route.RouteResultsetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 后端多节点执行器
 *
 * @author zhangtianlong
 */
public class MultiNodeExecutor extends MultiNodeHandler {

    private static final Logger logger = LoggerFactory.getLogger(MultiNodeExecutor.class);
    /**
     * 对应的前端连接
     */
    private FrontendSession session;
    /**
     * 影响的行数,Okay包需要使用
     */
    private volatile long affectedRows;
    /**
     * field Packet后面的EOF是否已经返回了
     */
    private volatile boolean fieldEofReturned;

    public MultiNodeExecutor(FrontendSession session) {
        fieldEofReturned = false;
        this.session = session;
    }

    @Override
    protected void reset(int initCount) {
        super.reset(initCount);
        affectedRows = 0L;
        fieldEofReturned = false;
    }

    @Override
    public void execute(RouteResultset rrs) {
        // 初始化nodeCount
        reset(rrs.getNodes().length);
        for (RouteResultsetNode node : rrs.getNodes()) {
            BackendConnection backend = session.getTarget(node);
            singleNodeExecute(backend, node);
        }
    }

    /**
     * 后端各个节点独自执行
     */
    private void singleNodeExecute(BackendConnection backend, RouteResultsetNode node) {
        // 转换成Command
        Command command = session.getSource().getFrontendCommand(node.getStatement(), node.getSqlType());
        // 加入队列
        backend.postCommand(command);
        // 触发
        backend.fireCmd();
    }

    @Override
    public void fieldListResponse(List<BinaryPacket> fieldList) {
        lock.lock();
        try {
            if (!isFailed.get()) {
                // 如果还没有传过fieldList,则传递
                if (!fieldEofReturned) {
                    writeFieldList(fieldList);
                    fieldEofReturned = true;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void writeFieldList(List<BinaryPacket> fieldList) {
        for (BinaryPacket bin : fieldList) {
            bin.packetId = ++packetId;
            bin.write(session.getCtx());
        }
        fieldList.clear();
    }

    @Override
    public void errorResponse(BinaryPacket bin) {
        ErrorPacket error = new ErrorPacket();
        error.read(bin);
        String errorMsg = new String(error.message);
        logger.error("errorMsg packet " + errorMsg);
        lock.lock();
        try {
            // 只要有一个error,就记录
            if (isFailed.compareAndSet(false, true)) {
                super.setFailMsg(errorMsg);
            }
            // 若是最后一个,则发送
            if (decrementNodeCount()) {
                notifyFailure();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void okResponse(BinaryPacket bin) {
        OkPacket ok = new OkPacket();
        ok.read(bin);
        lock.lock();
        try {
            affectedRows += ok.affectedRows;
            if (super.decrementNodeCount()) {
                // OK packet 只有在最后一个Okay到达 并且前面都不出错的时候才发送
                if (!isFailed.get()) {
                    ok.affectedRows = affectedRows;
                    logger.info("last insert id={}", ok.insertId);
                    session.getSource().setLastInsertId(ok.insertId);
                    ok.write(session.getCtx());
                    if (session.getSource().isAutoCommit()) {
                        session.release();
                    }
                } else {
                    notifyFailure();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void rowResponse(BinaryPacket bin) {
        lock.lock();
        try {
            if (!isFailed.get() && fieldEofReturned) {
                logger.info("row");
                bin.packetId = ++packetId;
                bin.write(session.getCtx());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void lastEofResponse(BinaryPacket bin) {
        lock.lock();
        try {
            logger.info("last eof");
            if (super.decrementNodeCount()) {
                if (!isFailed.get()) {
                    bin.packetId = ++packetId;
                    logger.info("write last eof");
                    bin.write(session.getCtx());
                    // 如果是自动提交,则释放session
                    if (session.getSource().isAutoCommit()) {
                        session.release();
                    }
                } else {
                    notifyFailure();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void notifyFailure() {
        ErrorPacket error = new ErrorPacket();
        error.message = errorMessage.getBytes();
        error.errno = errno;
        error.packetId = ++packetId;
        error.write(session.getCtx());
        // 如果是自动提交,则释放session
        if (session.getSource().isAutoCommit()) {
            session.release();
        }
    }


}