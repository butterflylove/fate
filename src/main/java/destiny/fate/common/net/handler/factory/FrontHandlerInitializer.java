package destiny.fate.common.net.handler.factory;

import destiny.fate.common.net.handler.backend.pool.MySqlDataSource;
import destiny.fate.common.net.handler.frontend.FrontendCollectHandler;
import destiny.fate.common.net.handler.frontend.FrontendConnection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 前端handler处理流程
 * @author zhangtianlong
 */
public class FrontHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private FrontConnectionFactory factory;

    public FrontHandlerInitializer(MySqlDataSource dataSource) {
        factory = new FrontConnectionFactory(dataSource);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        FrontendConnection source = factory.getConnection();
        FrontendCollectHandler collectHandler = new FrontendCollectHandler(source);

        ch.pipeline().addLast(collectHandler);
    }
}
