package destiny.fate.common.net.handler.node;

import destiny.fate.common.net.handler.session.FrontendSession;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.route.RouteResultset;

import java.util.List;

/**
 * Created by zhangtianlong on 17/11/19.
 */
public class SingleNodeExecutor implements ResponseHandler {

    private FrontendSession session;

    public SingleNodeExecutor(FrontendSession session) {
        this.session = session;
    }

    public void execute(RouteResultset rrs) {
        if (rrs.getNodes() == null || rrs.getNodes().length == 0) {

        }
    }

    public void fieldListResponse(List<BinaryPacket> fieldList) {

    }

    public void errorResponse(BinaryPacket bin) {

    }

    public void okResponse(BinaryPacket bin) {

    }

    public void rowResponse(BinaryPacket bin) {

    }

    public void lastEof(BinaryPacket bin) {

    }
}
