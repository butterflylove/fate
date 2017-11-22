package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.handler.backend.cmd.CmdType;
import destiny.fate.common.net.handler.backend.cmd.Command;
import destiny.fate.common.net.protocol.CommandPacket;
import destiny.fate.common.net.protocol.MySQLPacket;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhangtianlong
 */
public abstract class AbstractFrontendConnection {

    protected static final int PACKET_HEADER_SIZE = 4;

    public Command getFrontendCommand(String sql, int type) {
        CommandPacket packet = new CommandPacket();
        packet.packetId = 0;
        packet.command = MySQLPacket.COM_QUERY;
        packet.arg = sql.getBytes();
        Command cmd = new Command(packet, CmdType.FRONTEND_TYPE, type);
        return cmd;
    }

    protected ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
