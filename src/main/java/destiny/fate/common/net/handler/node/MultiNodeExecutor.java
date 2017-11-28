package destiny.fate.common.net.handler.node;

import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.route.RouteResultset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
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
    public void execute(RouteResultset rrs) {

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
