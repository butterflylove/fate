package destiny.fate.common.net.handler.frontend;

import destiny.fate.common.net.protocol.AuthPacket;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.Capabilities;
import destiny.fate.common.net.protocol.HandshakePacket;
import destiny.fate.common.net.protocol.OkPacket;
import destiny.fate.common.net.protocol.Versions;
import destiny.fate.common.util.RandomUtil;
import destiny.fate.common.util.SecurityUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

/**
 * 前端认证handler
 * @author zhangtianlong
 */
public class FrontendAuthenticator extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FrontendAuthenticator.class);

    public byte[] seed;

    protected FrontendConnection source;

    public FrontendAuthenticator(FrontendConnection source) {
        this.source = source;
    }

    /**
     * 发送HandShake包
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        source.setCtx(ctx);
        // 生成认证数据
        byte[] rand1 = RandomUtil.randomBytes(8);
        byte[] rand2 = RandomUtil.randomBytes(12);

        // 保存认证数据
        byte[] seed = new byte[rand1.length + rand2.length];
        System.arraycopy(rand1, 0, seed, 0, rand1.length);
        System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);
        this.seed = seed;

        // 发送握手数据包
        HandshakePacket hs = new HandshakePacket();
        hs.packetId = 0;
        hs.protocolVersion = Versions.PROTOCOL_VERSION;
        hs.serverVersion = Versions.SERVER_VERSION;
        hs.threadId = source.getId();
        hs.seed = rand1;
        hs.serverCapabilities = getServerCapabilities();
        hs.serverCharsetIndex = (byte) (source.charsetIndex & 0xff);
        hs.serverStatus = 2;
        hs.restOfScrambleBuff = rand2;
        logger.info("send handshake packet");
        hs.write(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BinaryPacket bin = (BinaryPacket) msg;
        logger.info("bin packet length={}, packetId={}", bin.packetLength, bin.packetId);
        logger.info("bin data={}", new String(bin.data));
        AuthPacket authPacket = new AuthPacket();
        authPacket.read(bin);
        // check password
        if (!checkPassword(authPacket.password, authPacket.user)) {
            logger.warn("access denied");
            return;
        }
        source.setUser(authPacket.user);
        source.setSchema(authPacket.database);
        source.setHost(((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress());
        source.setPort(((InetSocketAddress)ctx.channel().remoteAddress()).getPort());
        logger.info("auth success!");
        success(ctx);
    }

    private void success(final ChannelHandlerContext ctx) {
        // 认证成功, 替换handler
        ctx.pipeline().replace(this, "frontCommandHandler", new FrontendCommandHandler(source));
        ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(OkPacket.AUTH_OK);
        ctx.writeAndFlush(byteBuf);
    }

    protected boolean checkPassword(byte[] password, String user) {
        // TODO config
        String pass = "20040705WSH";
        if (pass == null || pass.length() == 0) {
            if (password == null || password.length == 0) {
                return true;
            } else {
                return false;
            }
        }
        if (password == null || password.length == 0) {
            return false;
        }

        // encrypt
        byte[] encryptPass = null;
        try {
            encryptPass = SecurityUtil.scramble411(pass.getBytes(), seed);
        } catch (NoSuchAlgorithmException e) {
            logger.error(source.toString(), e);
            return false;
        }
        if (encryptPass != null && (encryptPass.length == password.length)) {
            int i = encryptPass.length;
            while ((i--) != 0) {
                if (encryptPass[i] != password[i]) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    protected int getServerCapabilities() {
        int flag = 0;
        flag |= Capabilities.CLIENT_LONG_PASSWORD;
        flag |= Capabilities.CLIENT_FOUND_ROWS;
        flag |= Capabilities.CLIENT_LONG_FLAG;
        flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
        // flag |= Capabilities.CLIENT_NO_SCHEMA;
        // flag |= Capabilities.CLIENT_COMPRESS;
        flag |= Capabilities.CLIENT_ODBC;
        // flag |= Capabilities.CLIENT_LOCAL_FILES;
        flag |= Capabilities.CLIENT_IGNORE_SPACE;
        flag |= Capabilities.CLIENT_PROTOCOL_41;
        flag |= Capabilities.CLIENT_INTERACTIVE;
        // flag |= Capabilities.CLIENT_SSL;
        flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
        flag |= Capabilities.CLIENT_TRANSACTIONS;
        // flag |= ServerDefs.CLIENT_RESERVED;
        flag |= Capabilities.CLIENT_SECURE_CONNECTION;
        return flag;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    protected void failure(int errno, String info) {
        logger.error(source.toString() + info);
        // TODO
        return;
    }

}
