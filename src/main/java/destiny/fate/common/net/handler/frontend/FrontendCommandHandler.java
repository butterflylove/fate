package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.MySQLPacket;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指令 handler
 * @author zhangtianlong
 */
public class FrontendCommandHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FrontendCommandHandler.class);

    protected FrontendConnection source;

    public FrontendCommandHandler(FrontendConnection source) {
        this.source = source;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BinaryPacket bin = (BinaryPacket) msg;
        byte type = bin.data[0];
        switch (type) {
            case MySQLPacket.COM_INIT_DB:
                // just init the frontend
                logger.debug("COM_INIT_DB-----------");
                source.initDB(bin);
                break;
            case MySQLPacket.COM_QUERY:
                logger.debug("COM_QUERY-------");
                source.query(bin);
                break;
        }
    }
}
