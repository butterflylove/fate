package destiny.fate.common.net.protocol;

import destiny.fate.common.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public class BinaryPacket extends MySQLPacket {

    public static final byte OK = 1;
    public static final byte ERROR = 2;
    public static final byte HEADER = 3;
    public static final byte FIELD = 4;
    public static final byte FIELD_EOF = 5;
    public static final byte ROW = 6;
    public static final byte PACKET_EOF = 7;

    public byte[] data;

    @Override
    public void write(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer();
        BufferUtil.writeUB3(buf, packetLength);
        buf.writeByte(packetId);
        buf.writeBytes(data);
        ctx.writeAndFlush(buf);
    }

    @Override
    public int calcPacketSize() {
        return data == null ? 0 : data.length;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Binary Packet";
    }
}
