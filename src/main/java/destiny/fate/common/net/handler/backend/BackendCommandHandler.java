package destiny.fate.common.net.handler.backend;

import destiny.fate.common.net.exception.UnknownPacketException;
import destiny.fate.common.net.handler.backend.cmd.CmdType;
import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.parser.ServerParser;
import destiny.fate.common.net.protocol.OkPacket;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BinaryPacket bin = (BinaryPacket) msg;
        if (processCmd(ctx, bin)) {
            source.fireCmd();
        }
    }

    private boolean processCmd(ChannelHandlerContext ctx, BinaryPacket bin) {
        Command cmd = source.peekCommand();
        if (cmd.getSqlType() == ServerParser.SELECT || cmd.getSqlType() == ServerParser.SHOW) {
            selecting = true;
        } else {
            selecting = false;
        }
        // if handle success, the command will be removed
        if (handleResponse(bin, cmd.getType())) {
            source.pollCommand();
            return true;
        } else {
            return false;
        }
    }

    private boolean handleResponse(BinaryPacket bin, CmdType cmdType) {
        return handleNormalResult(bin, cmdType);
    }

    private boolean handleNormalResult(BinaryPacket bin, CmdType cmdType) {
        switch (bin.data[0]) {
            case OkPacket.FIELD_COUNT:
                if (cmdType == CmdType.BACKEND_TYPE) {
                    logger.info("backend command okay");
                } else {
                    logger.info("frontend command okay");
                }
                break;
            default:
                throw new UnknownPacketException(bin.toString());
        }
        return true;
    }


}
