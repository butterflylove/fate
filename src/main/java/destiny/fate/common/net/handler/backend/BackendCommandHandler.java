package destiny.fate.common.net.handler.backend;

import destiny.fate.common.net.exception.ErrorPacketException;
import destiny.fate.common.net.exception.UnknownPacketException;
import destiny.fate.common.net.handler.backend.cmd.CmdType;
import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.handler.frontend.FrontendConnection;
import destiny.fate.common.net.handler.node.ResponseHandler;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.EOFPacket;
import destiny.fate.common.net.protocol.ErrorPacket;
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
 *
 * @author zhangtianlong
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
     * 保存ResultSetHeader, Field, EOF包
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
            logger.info("handle command success.");
            source.pollCommand();
            return true;
        } else {
            logger.info("handle command don't finish.");
            return false;
        }
    }

    private boolean handleResponse(BinaryPacket bin, CmdType cmdType) {
        if (selecting) {
            logger.info("selecting execute");
            return handleResult(bin, cmdType);
        } else {
            logger.info("insert delete update execute");
            return handleNormalResult(bin, cmdType);
        }

    }

    private boolean handleResult(BinaryPacket bin, CmdType cmdType) {
        boolean result = false;
        int type = bin.data[0];
        switch (type) {
            case ErrorPacket.FIELD_COUNT:
                logger.info("result error.");
                result = true;
                break;
            case EOFPacket.FIELD_COUNT:
                EOFPacket eof = new EOFPacket();
                eof.read(bin);
                if (selectState == BackendConnResultState.RESULT_SET_FIELDS) {
                    logger.info("result first eof");
                    // Field Packet 和 eof 传输完毕, 下面接收 Row
                    selectState++;
                    selectState++;
                    fieldList.add(bin);
                    getResponseHandler().fieldListResponse(fieldList);
                } else {
                    logger.info("result last eof");
                    selecting = false;
                    selectState = BackendConnResultState.RESULT_SET_FIELD_COUNT;
                    fieldList.clear();
                    result = true;
                    getResponseHandler().lastEofResponse(bin);
                }
                break;
            default:
                switch (selectState) {
                    case BackendConnResultState.RESULT_SET_FIELD_COUNT:
                        logger.info("result header");
                        selectState++;
                        fieldList.add(bin);
                        break;
                    case BackendConnResultState.RESULT_SET_FIELDS:
                        logger.info("result fields");
                        fieldList.add(bin);
                        break;
                    case BackendConnResultState.RESULT_SET_ROW:
                        logger.info("result row");
                        getResponseHandler().rowResponse(bin);
                        break;
                }
        }
        return result;
    }

    private boolean handleNormalResult(BinaryPacket bin, CmdType cmdType) {
        byte flag = bin.data[0];
        logger.info("result flag = {}", Integer.toHexString(flag));
        switch (flag) {
            case OkPacket.FIELD_COUNT:
                if (cmdType == CmdType.BACKEND_TYPE) {
                    logger.info("backend command okay");
                } else {
                    logger.info("frontend command okay");
                    getResponseHandler().okResponse(bin);
                }
                break;
            case ErrorPacket.FIELD_COUNT:
                ErrorPacket err = new ErrorPacket();
                err.read(bin);
                if (cmdType == CmdType.BACKEND_TYPE) {
                    throw new ErrorPacketException("Command error message, message=" + new String(err.message));
                } else {
                    getResponseHandler().errorResponse(bin);
                }
            default:
                throw new UnknownPacketException(bin.toString());
        }
        return true;
    }

    private ResponseHandler getResponseHandler() {
        FrontendConnection frontendConnection = source.frontend;
        if (frontendConnection == null) {
            logger.info("frontendConnection is null");
        } else {
            logger.info("backend.frontend id={}", frontendConnection.getId());
        }
        return frontendConnection.getResponseHandler();
    }


}
