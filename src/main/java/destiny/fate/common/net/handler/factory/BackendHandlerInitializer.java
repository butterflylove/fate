package destiny.fate.common.net.handler.factory;

import destiny.fate.common.net.decoder.MySqlPacketDecoder;
import destiny.fate.common.net.handler.backend.BackendAuthenticator;
import destiny.fate.common.net.handler.backend.BackendConnection;
import destiny.fate.common.net.handler.backend.BackendHeadHandler;
import destiny.fate.common.net.handler.backend.BackendTailHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 后端handler处理流程
 * @author zhangtianlong
 */
public class BackendHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private BackendConnectionFactory factory;

    public BackendHandlerInitializer(BackendConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        BackendConnection source = factory.getConnection();
        BackendHeadHandler firstHandler = new BackendHeadHandler(source);
        BackendAuthenticator authenticator = new BackendAuthenticator(source);
        BackendTailHandler tailHandler = new BackendTailHandler(source);

        ch.pipeline().addLast(new MySqlPacketDecoder());
        ch.pipeline().addLast(BackendHeadHandler.HANDLER_NAME, firstHandler);
        ch.pipeline().addLast(authenticator);
        ch.pipeline().addLast(tailHandler);
    }
}
