package destiny.fate.common.net.handler.backend.cmd;

import destiny.fate.common.net.protocol.CommandPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * MySQL Command的包装类
 */
public class Command {

    /**
     * Command Packet
     */
    private CommandPacket cmdPacket;

    /**
     * command的type
     */
    private CmdType type;

    /**
     * sqlType,select | update | insert | delete
     */
    private int sqlType;

    public Command() {

    }

    public Command(CommandPacket cmdPacket, CmdType type, int sqlType) {
        this.cmdPacket = cmdPacket;
        this.type = type;
        this.sqlType = sqlType;
    }

    public ByteBuf getCmdByteBuf(ChannelHandlerContext ctx) {
        return cmdPacket.getByteBuf(ctx);
    }

    public CommandPacket getCmdPacket() {
        return cmdPacket;
    }

    public void setCmdPacket(CommandPacket cmdPacket) {
        this.cmdPacket = cmdPacket;
    }

    public CmdType getType() {
        return type;
    }

    public void setType(CmdType type) {
        this.type = type;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }
}
