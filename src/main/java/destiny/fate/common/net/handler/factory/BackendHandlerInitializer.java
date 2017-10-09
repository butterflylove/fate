package destiny.fate.common.net.handler.factory;

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

    }
}
