package destiny.fate.common.net.handler.backend;

import destiny.fate.common.net.protocol.BinaryPacket;
import io.netty.channel.ChannelHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 后端命令执行handler
 */
public class BackendCommandHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackendCommandHandler.class);

    private BackendConnection source;

    /**
     * 是否在select
     */
    private volatile boolean selecting;
    private volatile int selectState;
    /**
     * 保存fieldCount,field,EOF包
     */
    private List<BinaryPacket> fieldList;

    public BackendCommandHandler(BackendConnection source) {
        this.source = source;
        selecting = false;
        selectState = BackendConnResultState.RESULT_SET_FIELD_COUNT;
        fieldList = new LinkedList<BinaryPacket>();
    }
}
