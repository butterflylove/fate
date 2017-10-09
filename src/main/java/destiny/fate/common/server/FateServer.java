package destiny.fate.common.server;

import destiny.fate.common.config.ServerConfig;
import destiny.fate.common.net.handler.factory.FrontHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangtianlong01 on 2017/9/21.
 */
public class FateServer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FateServer.class);

    @Override
    public void run() {
        logger.info("Start the MySQL Sharding Proxy");
    }

    public static void main(String[] args) {

    }

    private void startServer() {
        // acceptor
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // worker
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new FrontHandlerInitializer());

            ChannelFuture future = bootstrap.bind(ServerConfig.SERVER_PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("端口{} 监听失败", ServerConfig.SERVER_PORT, e);
        }

    }
}
