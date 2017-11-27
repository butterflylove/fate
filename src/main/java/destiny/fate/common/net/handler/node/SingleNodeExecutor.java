package destiny.fate.common.net.handler.node;

import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.util.ErrorCode;
import destiny.fate.common.net.route.RouteResultset;
import destiny.fate.common.net.route.RouteResultsetNode;

import java.util.List;

/**
 * @author zhangtianlong
 */
public class SingleNodeExecutor implements ResponseHandler {

    private FrontendSession session;

    public SingleNodeExecutor(FrontendSession session) {
        this.session = session;
    }

    public void execute(RouteResultset rrs) {
        if (rrs.getNodes() == null || rrs.getNodes().length == 0) {
            session.writeErrMsg(ErrorCode.ERR_SINGLE_EXECUTE_NODES, "SingleNode executes no node");
        }
        if (rrs.getNodes().length > 1) {
            session.writeErrMsg(ErrorCode.ERR_SINGLE_EXECUTE_NODES, "SingleNode executes too many nodes");
        }
        // 获取RouteResultset对应的Backend
        BackendConnection backend = getBackend(rrs);
        RouteResultsetNode node = rrs.getNodes()[0];
        Command command = session.getSource().getFrontendCommand(node.getStatement(), node.getSqlType());
        backend.postCommand(command);

        backend.fireCmd();
    }

    public void fieldListResponse(List<BinaryPacket> fieldList) {
        writeFieldList(fieldList);
    }

    public void errorResponse(BinaryPacket bin) {

    }

    public void okResponse(BinaryPacket bin) {

    }

    public void rowResponse(BinaryPacket bin) {
        bin.write(session.getCtx());
    }

    public void lastEofResponse(BinaryPacket bin) {
        bin.write(session.getCtx());
        // TODO 回收后端连接
    }

    private BackendConnection getBackend(RouteResultset rrs) {
        return session.getTarget(rrs.getNodes()[0]);
    }

    private void writeFieldList(List<BinaryPacket> fieldList) {
        for (BinaryPacket bin : fieldList) {
            bin.write(session.getCtx());
        }
        fieldList.clear();
    }
}
