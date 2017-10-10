package destiny.fate.common.net.decoder;

import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangtianlong01 on 2017/10/10.
 */
public class MySqlPacketDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(MySqlPacketDecoder.class);

    private static final int PACKET_HEADER_SIZE = 4;

    private static final int MAX_PACKET_SIZE = 16 * 1024 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < PACKET_HEADER_SIZE) {
            return;
        }
        in.markReaderIndex();
        int packetLength = ByteUtil.readUB3(in);
        // 过载保护
        if (packetLength > MAX_PACKET_SIZE) {
            throw new IllegalArgumentException("Packet size over the limit " + MAX_PACKET_SIZE);
        }
        byte packetId = in.readByte();
        if (in.readableBytes() < packetLength) {
            // 回溯
            in.resetReaderIndex();
            return;
        }
        BinaryPacket packet = new BinaryPacket();
        packet.packetLength = packetLength;
        packet.packetId = packetId;
        packet.data = in.readBytes(packetLength).array();
        logger.info("packet length={}, packetId={}, data length={}", packetLength, packetId, packet.data.length);
        logger.info("data={}", new String(packet.data));
        if (packet.data == null || packet.data.length == 0) {
            logger.error("get data error, packet length={}", packet.packetLength);
        }
        out.add(packet);
    }
}
