package destiny.fate.common.net.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;


import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * @author zhangtianlong
 */
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFrameDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        channel.writeInbound(input.readBytes(2));
        channel.writeInbound(input.readBytes(7));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        ByteBuf x = read.duplicate();
        while (x.isReadable()) {
            System.out.print(x.readByte());
        }
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        x = read.duplicate();
        while (x.isReadable()) {
            System.out.print(x.readByte());
        }
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        x = read.duplicate();
        while (x.isReadable()) {
            System.out.print(x.readByte());
        }
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(-1 * i);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(buf));
        assertTrue(channel.finish());

        for (int i = 1; i < 10; i++) {
            assertEquals(i, channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }

    @Test
    public void testFrameChunkDecoderTest() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
        assertTrue(channel.writeInbound(input.readBytes(2)));
        try {
            channel.writeInbound(input.readBytes(4));
            Assert.fail();
        } catch (TooLongFrameException e) {

        }
        assertTrue(channel.writeInbound(input.readBytes(3)));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(2), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.skipBytes(4).readSlice(3), read);
        read.release();
        buf.release();
    }

    @Test
    public void test() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf sliced = buf.duplicate();
        System.out.println(sliced.toString(utf8));
        buf.setByte(0, (byte)'J');
        buf.readByte();
        System.out.println(buf.readerIndex());
        System.out.println(sliced.readerIndex());
        assertTrue(buf.getByte(0) == sliced.getByte(0));
    }
}
