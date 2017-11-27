package destiny.fate.common.net.handler.node;

import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.route.RouteResultset;

import java.util.List;

/**
 * @author zhangtianlong
 */
public interface ResponseHandler {
    /**
     * 执行sql
     */
    void execute(RouteResultset rrs);

    void fieldListResponse(List<BinaryPacket> fieldList);

    void errorResponse(BinaryPacket bin);

    void okResponse(BinaryPacket bin);

    void rowResponse(BinaryPacket bin);

    void lastEofResponse(BinaryPacket bin);
}
