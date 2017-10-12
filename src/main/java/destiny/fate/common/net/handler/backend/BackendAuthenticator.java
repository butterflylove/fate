package destiny.fate.common.net.handler.backend;

import destiny.fate.common.config.ServerConfig;
import destiny.fate.common.net.exception.ErrorPacketException;
import destiny.fate.common.net.exception.UnknowCharsetException;
import destiny.fate.common.net.exception.UnknownPacketException;
import destiny.fate.common.net.protocol.AuthPacket;
import destiny.fate.common.net.protocol.OkPacket;
import destiny.fate.common.net.protocol.ErrorPacket;
import destiny.fate.common.net.protocol.BinaryPacket;
import destiny.fate.common.net.protocol.Capabilities;
import destiny.fate.common.net.protocol.HandshakePacket;
import destiny.fate.common.net.protocol.util.CharsetUtil;
import destiny.fate.common.util.SecurityUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * 后端认证Handler
 * @author zhangtianlong
 */
public class BackendAuthenticator extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackendAuthenticator.class);

    private static final long CLIENT_FLAGS = getClientFlags();

    private static final long MAX_PACKET_SIZE = 1024 * 1024 * 16;

    private int state = BackendConnState.BACKEND_NOT_AUTHED;

    private BackendConnection source;

    public BackendAuthenticator(BackendConnection source) {
        this.source = source;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        switch (state) {
            case (BackendConnState.BACKEND_NOT_AUTHED):
                // init ctx
                source.setCtx(ctx);
                // 处理Handshake包并发送auth包
                auth(ctx, msg);
                // 更新连接状态
                this.state = BackendConnState.BACKEND_AUTHED;
                break;
            case (BackendConnState.BACKEND_AUTHED):
                authOk(ctx, msg);
                break;
            default:

        }
    }

    private void authOk(ChannelHandlerContext ctx, Object msg) {
        BinaryPacket bin = (BinaryPacket) msg;
        switch (bin.data[0]) {
            case OkPacket.FIELD_COUNT:
                logger.info("Auth success!");
                afterSuccess();
                break;
            case ErrorPacket.FIELD_COUNT:
                logger.info("auth failed.");
                ErrorPacket err = new ErrorPacket();
                err.read(bin);
                throw new ErrorPacketException("AUTH not Ok!");
            default:
                throw new UnknownPacketException(bin.toString());
        }

        // 用CommandHandler替换Authenticator
        ctx.pipeline().replace(this, "BackendCommandHandler", new BackendCommandHandler(source));
    }

    private void afterSuccess() {
        // TODO
        logger.info("auth ok");
    }

    private void auth(ChannelHandlerContext ctx, Object msg) {
        HandshakePacket handshakePacket = new HandshakePacket();
        handshakePacket.read((BinaryPacket) msg);
        source.setId(handshakePacket.threadId);
        int charsetIndex = handshakePacket.serverCharsetIndex & 0xff;
        if ((source.charset = CharsetUtil.getCharset(charsetIndex)) != null) {
            source.charsetIndex = charsetIndex;
        } else {
            throw new UnknowCharsetException("charset:" + charsetIndex);
        }
        try {
            auth(handshakePacket, ctx);
        } catch (Exception e) {
            logger.error("auth packet error", e);
        }
    }

    private void auth(HandshakePacket hsp, ChannelHandlerContext ctx)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        AuthPacket ap = new AuthPacket();
        ap.clientFlags = CLIENT_FLAGS;
        ap.maxPacketSize = MAX_PACKET_SIZE;
        ap.charsetIndex = source.charsetIndex;
        // TODO config
        ap.user = ServerConfig.USER_NAME;
        String passwd = ServerConfig.PASS_WORD;
        if (passwd != null && passwd.length() > 0) {
            byte[] password = passwd.getBytes(source.charset);
            byte[] seed = hsp.seed;
            byte[] restOfScramble = hsp.restOfScrambleBuff;
            byte[] authSeed = new byte[seed.length + restOfScramble.length];
            System.arraycopy(seed, 0, authSeed, 0, seed.length);
            System.arraycopy(restOfScramble, 0, authSeed, seed.length, restOfScramble.length);
            ap.password = SecurityUtil.scramble411(password, authSeed);
        }
        // TODO config
        ap.database = ServerConfig.DATABASE;
        ap.write(ctx);
    }

    /**
     * 与MySQL连接时的一些特性指定
     */
    private static long getClientFlags() {
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
        // flag |= Capabilities.CLIENT_RESERVED;
        flag |= Capabilities.CLIENT_SECURE_CONNECTION;
        // client extension
        // 不允许MULTI协议
        // flag |= Capabilities.CLIENT_MULTI_STATEMENTS;
        // flag |= Capabilities.CLIENT_MULTI_RESULTS;
        return flag;
    }
}
