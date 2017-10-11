package destiny.fate.common.net.response;

import destiny.fate.common.net.handler.frontend.FrontendConnection;
import destiny.fate.common.net.protocol.EOFPacket;
import destiny.fate.common.net.protocol.FieldPacket;
import destiny.fate.common.net.protocol.ResultSetHeaderPacket;
import destiny.fate.common.net.protocol.RowDataPacket;
import destiny.fate.common.net.protocol.util.Fields;
import destiny.fate.common.net.protocol.util.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public class SelectVersionComment {

    private static final byte[] VERSION_COMMENT = "Fate Server-1.0 author:zhangtianlong".getBytes();
    private static final int FIELD_COUNT = 1;
    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[] fields =new FieldPacket[FIELD_COUNT];
    private static final EOFPacket eof = new EOFPacket();

    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        fields[i] = PacketUtil.getField("@@VERSION_COMMENT", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        eof.packetId = ++packetId;
    }

    public static void response(FrontendConnection c) {
        ChannelHandlerContext ctx = c.getCtx();

        ByteBuf buffer = ctx.alloc().buffer();
        // write header
        buffer = header.writeBuf(buffer, ctx);
        // write fields
        for (FieldPacket field : fields) {
            buffer = field.writeBuf(buffer, ctx);
        }
        // write eof
        buffer = eof.writeBuf(buffer, ctx);
        // write rows
        byte packetId = eof.packetId;
        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
        row.add(VERSION_COMMENT);
        row.packetId = ++packetId;
        buffer = row.writeBuf(buffer, ctx);
        // write last eof
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.writeBuf(buffer, ctx);

        ctx.writeAndFlush(buffer);
    }
}
