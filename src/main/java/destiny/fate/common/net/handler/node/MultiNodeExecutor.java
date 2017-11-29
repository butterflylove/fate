package destiny.fate.common.net.handler.node;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.route.RouteResultset;
import destiny.fate.common.net.route.RouteResultsetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    }

    @Override
    public void errorResponse(BinaryPacket bin) {

    }

    @Override
    public void okResponse(BinaryPacket bin) {

    }

    @Override
    public void rowResponse(BinaryPacket bin) {

    }

    @Override
    public void lastEofResponse(BinaryPacket bin) {

    }
}