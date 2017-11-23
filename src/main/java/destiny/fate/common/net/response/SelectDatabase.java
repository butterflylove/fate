package destiny.fate.common.net.response;

import destiny.fate.common.net.handler.frontend.FrontendConnection;
import destiny.fate.common.net.protocol.EOFPacket;
import destiny.fate.common.net.protocol.FieldPacket;
import destiny.fate.common.net.protocol.ResultSetHeaderPacket;
import destiny.fate.common.net.protocol.RowDataPacket;
import destiny.fate.common.net.protocol.util.Fields;
import destiny.fate.common.net.protocol.util.PacketUtil;
import destiny.fate.common.net.protocol.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理 SELECT DATABASE()
 *
 * @author zhangtianlong
 */
public class SelectDatabase {

    private static final int FIELD_COUNT = 1;
    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
    private static final EOFPacket eof = new EOFPacket();
    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        fields[i] = PacketUtil.getField("DATABASE()", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        eof.packetId = ++packetId;
    }

    public static void response(FrontendConnection c) {
        ChannelHandlerContext ctx = c.getCtx();
        ByteBuf buffer = ctx.alloc().buffer();
        buffer = header.writeBuf(buffer, ctx);
        for (FieldPacket field : fields) {
            buffer = field.writeBuf(buffer, ctx);
        }
        buffer = eof.writeBuf(buffer, ctx);
        byte packetId = eof.packetId;
        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
        row.add(StringUtil.encode(c.getSchema(), c.getCharset()));
        row.packetId = ++packetId;
        buffer = row.writeBuf(buffer, ctx);
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.writeBuf(buffer, ctx);
        ctx.writeAndFlush(buffer);
    }
}
